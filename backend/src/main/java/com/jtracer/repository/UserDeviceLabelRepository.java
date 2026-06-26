package com.jtracer.repository;

import com.jtracer.domain.entity.UserDeviceLabel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDeviceLabelRepository extends JpaRepository<UserDeviceLabel, String> {

    Optional<UserDeviceLabel> findFirstByMacAddressOrderByUpdatedAtDesc(String macAddress);
}
