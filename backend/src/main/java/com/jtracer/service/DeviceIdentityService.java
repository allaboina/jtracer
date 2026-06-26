package com.jtracer.service;

import com.jtracer.domain.entity.LanDevice;
import com.jtracer.domain.enums.DeviceType;
import com.jtracer.dto.collector.LanDeviceRawDto;

/**
 * Applies device identity rules and user labels to LAN devices.
 */
public interface DeviceIdentityService {

    /**
     * Resolves identity signals and attaches classification to a LAN device.
     */
    void enrichDevice(LanDevice device, LanDeviceRawDto raw);

    /**
     * Stores a user-provided label that overrides automatic classification.
     */
    void applyUserLabel(LanDevice device, String label, DeviceType deviceType);
}
