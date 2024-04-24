package fr.house.Services;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;

import fr.house.Repositories.DeviceRepository;

import fr.house.Beans.Device;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
public class DockerService {
    // Name of the Pi-hole container
    private final String CONTAINER_NAME = "pihole";
    // Path to the DHCP configuration file
    private final String DHCP_CONF_FILE_PATH = "/etc/dnsmasq.d/04-pihole-static-dhcp.conf";

    @Autowired
    private final DeviceRepository deviceRepository;

    /**
     * This method returns the content of the DHCP configuration file of a Pi-hole container (named "pihole")
     * @return {String} The content of the DHCP configuration file
     */
    public String fetchDhcpFileConfigContent() {
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
                    System.out.println("Error occurred");
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return output.toString();
    }

    public Set<Device> extractDevicesInformations() {
        Set<Device> devices = new HashSet<>();
        String result = fetchDhcpFileConfigContent();
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
        return devices;
    }

    @Transactional
    protected void updateDevicesInDatabase(Set<Device> devices) {
        for (Device device : devices) {
            Device existingDevice = deviceRepository.findByMac(device.getMac());
            if (existingDevice == null) {
                deviceRepository.save(device);
            } else {
                existingDevice.setIp(device.getIp());
                existingDevice.setHostname(device.getHostname());
                deviceRepository.save(existingDevice);
            }
        }
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    protected void refreshDeviceDatabase() {
        Set<Device> devices = extractDevicesInformations();
        updateDevicesInDatabase(devices);
    }

}
