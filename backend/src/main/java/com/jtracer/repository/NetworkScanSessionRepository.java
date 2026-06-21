package com.jtracer.repository;

import com.jtracer.domain.entity.NetworkScanSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NetworkScanSessionRepository extends JpaRepository<NetworkScanSession, String> {
}
