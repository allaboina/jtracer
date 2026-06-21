package com.jtracer.collector.common;

import com.jtracer.dto.collector.ConnectionSnapshotDto;
import java.util.List;

/**
 * Platform adapter for collecting outbound network connection metadata.
 */
public interface NetworkConnectionProvider {

    List<ConnectionSnapshotDto> collectConnections();
}
