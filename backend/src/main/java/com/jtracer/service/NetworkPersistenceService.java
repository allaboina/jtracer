package com.jtracer.service;

import com.jtracer.domain.entity.ObservationSession;
import com.jtracer.dto.collector.ConnectionSnapshotDto;
import java.util.List;

/**
 * Persists normalized network connections, endpoints, and domain identities.
 */
public interface NetworkPersistenceService {

    int persistConnectionCollection(ObservationSession session, List<ConnectionSnapshotDto> connections);
}
