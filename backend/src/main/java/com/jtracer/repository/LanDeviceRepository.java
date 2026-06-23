package com.jtracer.repository;

import com.jtracer.domain.entity.LanDevice;
import com.jtracer.domain.enums.DeviceStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanDeviceRepository extends JpaRepository<LanDevice, String> {

    Optional<LanDevice> findByMacAddress(String macAddress);

    Optional<LanDevice> findByIpAddress(String ipAddress);

    List<LanDevice> findByStatusIn(List<DeviceStatus> statuses);

    long countByStatus(DeviceStatus status);
}
