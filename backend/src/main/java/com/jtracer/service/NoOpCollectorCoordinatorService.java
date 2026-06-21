package com.jtracer.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(CollectorCoordinatorService.class)
public class NoOpCollectorCoordinatorService implements CollectorCoordinatorService {

    @Override
    public void startCollectors() {
        // Collectors are only available on supported host platforms.
    }

    @Override
    public void stopCollectors() {
        // No-op on unsupported platforms.
    }
}
