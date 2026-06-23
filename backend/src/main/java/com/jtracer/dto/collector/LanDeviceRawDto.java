package com.jtracer.dto.collector;

import com.jtracer.domain.enums.Confidence;

/**
 * Raw LAN device discovered from ARP or ping before persistence.
 */
public class LanDeviceRawDto {

    private String ipAddress;
    private String macAddress;
    private String hostname;
    private String adapterName;
    private Confidence confidence = Confidence.UNKNOWN;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getAdapterName() {
        return adapterName;
    }

    public void setAdapterName(String adapterName) {
        this.adapterName = adapterName;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public void setConfidence(Confidence confidence) {
        this.confidence = confidence;
    }
}
