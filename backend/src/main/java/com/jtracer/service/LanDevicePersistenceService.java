package com.jtracer.service;

import com.jtracer.domain.entity.ObservationSession;
import com.jtracer.dto.collector.NetworkScanResultDto;

/**
 * Persists LAN scan sessions and discovered devices.
 */
public interface LanDevicePersistenceService {

    int persistScanResult(ObservationSession session, NetworkScanResultDto scanResult);
}
