package com.jtracer.collector.common;

import com.jtracer.dto.collector.ProcessMetricSnapshotDto;
import java.util.List;

/**
 * Platform adapter for collecting process resource metrics.
 */
public interface ProcessMetricProvider {

    List<ProcessMetricSnapshotDto> collectMetrics();
}
