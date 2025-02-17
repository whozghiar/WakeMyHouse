package fr.house.Repositories;

import fr.house.Beans.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query("SELECT d FROM Device d WHERE d.mac = :mac")
    Device findByMac(String mac);

    @Query("SELECT d FROM Device d WHERE d.hostname = :hostname")
    Device findByName(String hostname);

    @Query("SELECT d FROM Device d WHERE d.ip = :ip")
    Device findByIp(String ip);

}
