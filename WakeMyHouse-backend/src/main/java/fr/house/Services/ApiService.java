package fr.house.Services;

import fr.house.Beans.Device;
import fr.house.Exceptions.DatabaseException;
import fr.house.Repositories.DeviceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ApiService {
    private static final Logger log = LoggerFactory.getLogger(ApiService.class);
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private NetworkService networkService;


    /**
     * This method is used to get all devices from the database
     * @throws DatabaseException if an error occurs while getting devices from the database
     * @return
     */
    public Set<Device> getAllDevices() {
        try{
            return new HashSet<>(deviceRepository.findAll());
        } catch (Exception e) {
            throw new DatabaseException("Error while getting devices from database", e);
        }
    }

    /**
     * This method is used to get a device by its id
     * @param id
     * @return
     */
    public Device getDeviceById(Long id) {
        Device device = deviceRepository.findById(id).orElse(null);
        if (device == null) {
            throw new DatabaseException("Device with id " + id + " not found", null);
        }
        return device;
    }

    @Transactional
    public void addDevice(Device device) {
        try{
            deviceRepository.save(device);
        } catch (Exception e) {
            throw new DatabaseException("Error while saving device to database", e);
        }
    }

    /**
     * This method is used to refresh the status of a device
     * @param {Long} deviceId - the id of the device to refresh
     * @return
     */
    public Device refreshDeviceStatus(Long deviceId) {
        Device device = deviceRepository.findById(deviceId).orElse(null);
        if (device != null) {
            log.info("Refreshing device with id: " + deviceId);
            boolean isReachable = networkService.pingHost(device.getIp(), 1000);
            device.setStatus(isReachable);
            addDevice(device);

        }
        return device;
    }

    /**
     * This method is used to power on or off a device
     * @param deviceId
     * @param powerOn
     */
    public Device powerDevice(Long deviceId, boolean powerOn) {
        Device device = deviceRepository.findById(deviceId).orElse(null);
        if (device != null && ((device.getStatus() && !powerOn) || (!device.getStatus() && powerOn))) {
            if (powerOn) {
                log.info("Turning on device with id: " + deviceId);
                networkService.sendWakeOnLanPacket(device.getIp(), device.getMac());
            } else {
                // Logique pour éteindre la machine
                log.info("Turning off device with id: " + deviceId);
            }
            refreshDeviceStatus(deviceId); // Refresh device status after power on/off
        }
        return device;
    }
}
