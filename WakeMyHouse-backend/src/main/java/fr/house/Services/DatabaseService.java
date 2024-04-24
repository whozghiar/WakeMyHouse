package fr.house.Services;

import fr.house.Beans.Device;
import fr.house.Exceptions.DatabaseException;
import fr.house.Exceptions.DeviceException;
import fr.house.Repositories.DeviceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseService {

    // Name of the Pi-hole container
    private final String CONTAINER_NAME = "pihole";
    // Path to the DHCP configuration file
    private final String DHCP_CONF_FILE_PATH = "/etc/dnsmasq.d/04-pihole-static-dhcp.conf";

    @Autowired
    private final DeviceRepository deviceRepository;

    @Autowired
    private NetworkService networkService;

    /**
     * This method returns the content of the DHCP configuration file of a Pi-hole container (named "pihole")
     * @return {String} The content of the DHCP configuration file
     */
    protected String fetchDhcpFileConfigContent() {
        List<String> commands = new ArrayList<>();
        commands.add("docker");
        commands.add("exec");
        commands.add(CONTAINER_NAME);
        commands.add("cat");
        commands.add(DHCP_CONF_FILE_PATH);

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        StringBuilder output = new StringBuilder();
        Process process = null;

        try {
            process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line + "\n");
                }
                int exitVal = process.waitFor();
                if (exitVal != 0) {
                    // Handle the case where the process execution fails
                    log.error("Error while fetching the DHCP configuration file content");
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new DeviceException("Error while fetching the DHCP configuration file content", e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return output.toString();
    }

    /**
     * This method extracts the devices informations from the DHCP configuration file of a Pi-hole container
     * It maps the devices informations to a set of Device objects
     * @return {Set<Device>} A set of devices
     */
    protected Set<Device> extractDevicesInformations() {
        Set<Device> devices = new HashSet<>();
        String result = fetchDhcpFileConfigContent();

        try{
            String[] lines = result.split("\\n");

            for (String line : lines) {
                if (line.startsWith("dhcp-host=")) {
                    String[] parts = line.substring(10).split(",");
                    String mac = parts[0];
                    String ip = parts[1];
                    String hostname = parts[2];
                    Device device = new Device();
                    device.setMac(mac);
                    device.setIp(ip);
                    device.setHostname(hostname);
                    devices.add(device);
                }
            }
        } catch (Exception e) {
            throw new DatabaseException("Error while extracting devices informations", e);
        }
        return devices;
    }

    /**
     * This method checks if a device exists in the database
     * @param device
     * @return {boolean} True if the device exists in the database, false otherwise
     */
    protected boolean isDeviceExists(Device device) {
        return deviceRepository.findByMac(device.getMac()) != null;
    }

    @Transactional
    protected void addDevice(Device device) {
        try{
            deviceRepository.save(device);
        }catch (Exception e) {
            throw new DatabaseException("Error while adding device with MAC address " + device.getMac(), e);
        }
    }

    /**
     * This method updates the informations of a device in the database
     * @param {Device} device - The device to update
     */
    @Transactional
    protected void updateDevice(Device device) {
        Device existingDevice = deviceRepository.findByMac(device.getMac());
        if (existingDevice == null) {
            throw new DatabaseException("Device with MAC address " + device.getMac() + " not found in the database",null);
        }
        existingDevice.setIp(device.getIp());
        existingDevice.setHostname(device.getHostname());
        existingDevice.setStatus(device.getStatus());

        try{
            deviceRepository.save(existingDevice);
        }catch (Exception e) {
            throw new DatabaseException("Error while updating device with MAC address " + device.getMac(), e);
        }
    }

    @Transactional
    protected void addOrUpdateDevice(Device device) {
        boolean status = networkService.pingHost(device.getIp(), 2000); // Ping pour vérifier le statut
        log.info("Device " + device.getHostname() + " with MAC address " + device.getMac() + " is " + (status ? "up" : "down"));
        device.setStatus(status);

        if (!isDeviceExists(device)) {
            try{
                deviceRepository.save(device);
            }catch (Exception e) {
                throw new DatabaseException("Error while adding device with MAC address " + device.getMac(), e);
            }

        } else {
            updateDevice(device);
        }
    }

    /**
     * This method is scheduled to run every 5 minutes
     * It fetches the devices informations from the DHCP configuration file of a Pi-hole container
     */
    @Scheduled(fixedRate = 10000)
    @Transactional
    protected void autoFillDatabase() {
        Set<Device> devices = extractDevicesInformations();
        devices.forEach(device -> {
            addOrUpdateDevice(device);
        });
    }




}
