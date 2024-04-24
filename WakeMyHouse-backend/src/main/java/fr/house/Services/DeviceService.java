package fr.house.Services;

import fr.house.Exceptions.DatabaseException;
import fr.house.Exceptions.DeviceException;
import fr.house.Repositories.DeviceRepository;

import fr.house.Beans.Device;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;




    /**
     * This method pings a device to check if it is reachable
     * @param {Device} device The device to ping
     * @return {boolean} True if the device is reachable, false otherwise
     */
    protected boolean pingDevice(Long id){
        Device device = deviceRepository.findById(id).orElseThrow(() -> new DeviceException("Device not found", new IncorrectResultSizeDataAccessException(1, 0)));
        try {
            InetAddress address = InetAddress.getByName(device.getIp());
            // Vérifie si l'appareil répond dans un délai de 2 secondes
            return address.isReachable(2000);
        } catch (Exception e) {
            throw new DeviceException("Error while pinging the ip address", e);
        }
    }

}
