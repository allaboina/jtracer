package com.jtracer.service;

import com.jtracer.domain.entity.ObservationSession;
import com.jtracer.dto.collector.SystemHealthCollectorDto;

/**
 * Persists system health snapshots from collectors.
 */
public interface SystemHealthService {

    void persistSystemHealth(ObservationSession session, SystemHealthCollectorDto health);
}
