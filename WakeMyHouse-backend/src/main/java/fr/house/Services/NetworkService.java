package fr.house.Services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

@Service
@RequiredArgsConstructor
@Slf4j
public class NetworkService {
    public boolean pingHost(String address, int timeout) {
        try {
            InetAddress inet = InetAddress.getByName(address);
            log.info("Trying to ping the host: " + address);
            return inet.isReachable(timeout);
        } catch (IOException e) {
            log.error("An error occurred while trying to ping the host", e);
            return false;
        } catch (Exception e) {
            log.error("An error occurred while trying to ping the host", e);
            return false;
        }
    }

    public void sendWakeOnLanPacket(String ip, String mac) {
        // Utilisation de la librairie Java pour envoyer un paquet WOL
    }

    public void updateDeviceStatusAutomatically() {
        // Implémenter la logique pour mettre à jour le statut des appareils
    }
}
