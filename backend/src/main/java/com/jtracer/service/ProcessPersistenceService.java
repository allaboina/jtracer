package com.jtracer.service;

import com.jtracer.domain.entity.ObservationSession;
import com.jtracer.dto.collector.ProcessMetricSnapshotDto;
import com.jtracer.dto.collector.ProcessSnapshotDto;
import java.util.List;

/**
 * Persists normalized process and metric snapshots from collectors.
 */
public interface ProcessPersistenceService {

    void persistProcessCollection(
            ObservationSession session,
            List<ProcessSnapshotDto> snapshots,
            List<ProcessMetricSnapshotDto> metrics);
}
