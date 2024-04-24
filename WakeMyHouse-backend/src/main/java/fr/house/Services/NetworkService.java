package fr.house.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;

@Service
@RequiredArgsConstructor
public class NetworkService {
    public boolean pingHost(String address, int timeout) {
        try {
            InetAddress inet = InetAddress.getByName(address);
            return inet.isReachable(timeout);
        } catch (IOException e) {
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
