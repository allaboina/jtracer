package com.jtracer.repository;

import com.jtracer.domain.entity.HostMachine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostMachineRepository extends JpaRepository<HostMachine, String> {
}
