package com.jtracer.collector.common;

import com.jtracer.dto.collector.ProcessSnapshotDto;
import java.util.List;

/**
 * Platform adapter for collecting running process snapshots.
 */
public interface ProcessSnapshotProvider {

    List<ProcessSnapshotDto> collectProcesses();
}
