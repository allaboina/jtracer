package com.jtracer.repository;

import com.jtracer.domain.entity.SystemHealthSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemHealthSnapshotRepository extends JpaRepository<SystemHealthSnapshot, String> {

    java.util.Optional<SystemHealthSnapshot> findFirstBySession_IdOrderBySampledAtDesc(String sessionId);
}
