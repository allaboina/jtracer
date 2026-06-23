package com.jtracer.dto.collector;

import java.util.ArrayList;
import java.util.List;

/**
 * Normalized LAN scan result from a platform adapter before persistence.
 */
public class NetworkScanResultDto {

    private SubnetInfoDto subnet;
    private List<LanDeviceRawDto> devices = new ArrayList<>();
    private long scanDurationMs;
    private String sourceAdapter = "macos-arp";

    public SubnetInfoDto getSubnet() {
        return subnet;
    }

    public void setSubnet(SubnetInfoDto subnet) {
        this.subnet = subnet;
    }

    public List<LanDeviceRawDto> getDevices() {
        return devices;
    }

    public void setDevices(List<LanDeviceRawDto> devices) {
        this.devices = devices;
    }

    public long getScanDurationMs() {
        return scanDurationMs;
    }

    public void setScanDurationMs(long scanDurationMs) {
        this.scanDurationMs = scanDurationMs;
    }

    public String getSourceAdapter() {
        return sourceAdapter;
    }

    public void setSourceAdapter(String sourceAdapter) {
        this.sourceAdapter = sourceAdapter;
    }
}
