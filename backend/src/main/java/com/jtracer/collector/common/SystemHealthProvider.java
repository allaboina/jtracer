package com.jtracer.collector.common;

import com.jtracer.dto.collector.SystemHealthCollectorDto;

/**
 * Platform adapter for machine-level health metrics (CPU, memory, disk, battery).
 */
public interface SystemHealthProvider {

    SystemHealthCollectorDto collectSystemHealth();
}
