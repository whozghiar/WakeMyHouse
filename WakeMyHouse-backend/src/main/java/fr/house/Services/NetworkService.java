package fr.house.Services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@Service
@RequiredArgsConstructor
@Slf4j
public class NetworkService {
    public static final int PORT = 9;

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

    /**
     * This method sends a Wake-on-LAN packet to the specified IP and MAC address.
     *
     * @param ipStr  The IP address to which the Wake-on-LAN packet should be sent.
     * @param macStr The MAC address to which the Wake-on-LAN packet should be sent.
     */
    public void sendWakeOnLanPacket(String ipStr, String macStr) {
        try {
            byte[] macBytes = getMacBytes(macStr);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            InetAddress address = InetAddress.getByName(ipStr);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();

            log.info("Wake-on-LAN packet sent.");
        } catch (Exception e) {
            log.error("Failed to send Wake-on-LAN packet: ", e);
        }
    }

    /**
     * This method converts a MAC address string into a byte array.
     *
     * @param macStr The MAC address string.
     * @return The MAC address as a byte array.
     * @throws IllegalArgumentException If the MAC address is invalid.
     */
    private byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }
}
