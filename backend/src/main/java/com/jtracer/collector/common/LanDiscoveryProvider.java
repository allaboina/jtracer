package com.jtracer.collector.common;

import com.jtracer.dto.collector.NetworkScanResultDto;

/**
 * Platform adapter for LAN device discovery.
 */
public interface LanDiscoveryProvider {

    NetworkScanResultDto scanLocalNetwork();
}
