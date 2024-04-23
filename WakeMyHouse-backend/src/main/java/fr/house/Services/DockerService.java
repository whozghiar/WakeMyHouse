package fr.house.Services;

import fr.house.Repositories.DeviceRepository;
import com.github.dockerjava.api.command.InspectContainerResponse;
import fr.house.Beans.Device;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DockerClientBuilder;
import fr.house.Exceptions.DockerException;

import java.util.HashSet;
import java.util.Set;

public class DockerService {
    private final String CONTAINER_NAME = "pihole";
    private final String DHCP_CONF_FILE_PATH = "/etc/dnsmasq.d/04-pihole-static-dhcp.conf";

    private DeviceRepository deviceRepository;

    private Container getContainer(){
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        Container container = dockerClient.listContainersCmd().exec().stream()
                .filter(c -> c.getNames()[0].equals(CONTAINER_NAME))
                .findFirst()
                .orElse(null);

        if (container == null) {
            throw new DockerException("The container " + CONTAINER_NAME + " cannot be accessed", "Container not found");
        }

        return container;
    }

    /**
     *
     * @return
     */
    private Set<Device> extractDhcpLeases() {
        // Get all devices from the DHCP configuration file
        return null;
    }

}
