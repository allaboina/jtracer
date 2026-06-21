package com.jtracer.repository;

import com.jtracer.domain.entity.ObservationSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObservationSessionRepository extends JpaRepository<ObservationSession, String> {
}
