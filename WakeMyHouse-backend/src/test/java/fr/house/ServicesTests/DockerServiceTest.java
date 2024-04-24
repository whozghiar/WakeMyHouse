package fr.house.ServicesTests;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.transport.DockerHttpClient;

import fr.house.Beans.Device;
import fr.house.Services.DockerService;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class DockerServiceTest {

    private static DockerService service = new DockerService();

    @Test
    public void testGetAllContainers()
    {
        Set <Device> devices;
        devices = service.extractDevicesInformations();

        devices.forEach(System.out::println);

    }

}
