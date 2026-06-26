package com.jtracer.collector.common;

import com.jtracer.dto.collector.DeviceIdentityResultDto;
import com.jtracer.dto.collector.LanDeviceRawDto;

/**
 * Resolves human-readable device identity from raw LAN device signals.
 */
public interface DeviceIdentityResolver {

    DeviceIdentityResultDto resolveIdentity(LanDeviceRawDto rawDevice);

    default DeviceIdentityResultDto resolveIdentity(LanDeviceRawDto rawDevice, String userLabel) {
        return resolveIdentity(rawDevice);
    }
}
