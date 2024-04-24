package fr.house;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WakeTheHouseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WakeTheHouseBackendApplication.class, args);
    }

}
