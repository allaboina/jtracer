package com.jtracer.service;

import com.jtracer.api.v1.dto.SystemHealthDataDto;
import java.util.Optional;

/**
 * Read-only queries for system health API responses.
 */
public interface SystemHealthQueryService {

    Optional<SystemHealthDataDto> getCurrentHealth();
}
