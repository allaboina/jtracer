package com.jtracer.repository;

import com.jtracer.domain.entity.DeviceIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceIdentityRepository extends JpaRepository<DeviceIdentity, String> {
}
