package com.jtracer.repository;

import com.jtracer.domain.entity.DeviceIdentityRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceIdentityRuleRepository extends JpaRepository<DeviceIdentityRule, String> {
}
