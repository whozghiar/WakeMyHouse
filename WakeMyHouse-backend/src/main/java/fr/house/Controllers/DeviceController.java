package fr.house.Controllers;

import fr.house.Beans.Device;
import fr.house.Services.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;

import java.util.Set;

@RestController
@RequestMapping("/api/devices")
@Slf4j
public class DeviceController {

    @Autowired
    private ApiService apiService;

    @GetMapping
    @Operation(summary = "Récupérer tous les appareils",
            description = "Récupérer tous les appareils du réseau."
    )
    public ResponseEntity<?> getAllDevices() {
        log.info("Récupération de tous les appareils");
        Set<Device> devices = apiService.getAllDevices();
        log.info("Appareils récupérés avec succès : \n\t" + devices);
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    @PostMapping("/{id}/refresh")
    public ResponseEntity<Device> refreshDeviceStatus(@PathVariable Long id) {
        log.info("Rafraîchissement de l'appareil " + id);
        Device device = apiService.refreshDeviceStatus(id);
        return new ResponseEntity<>(device, HttpStatus.OK);
    }

    @PostMapping("/{id}/power")
    public ResponseEntity<Device> powerDevice(@PathVariable Long id, @RequestBody Boolean powerOn) {
        log.info("Changement de l'état de l'appareil " + id + " à " + powerOn);
        Device device = apiService.powerDevice(id, powerOn);
        return new ResponseEntity<>(device, HttpStatus.OK);
    }
}
