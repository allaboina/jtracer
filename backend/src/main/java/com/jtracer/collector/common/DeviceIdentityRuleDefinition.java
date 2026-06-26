package com.jtracer.collector.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceIdentityRuleDefinition {

    private String ruleName;
    private List<String> vendorContains = new ArrayList<>();
    private List<String> hostnameContains = new ArrayList<>();
    private List<String> mdnsContains = new ArrayList<>();
    private String deviceType;
    private String displayName;
    private int confidenceScore;
    private int priority;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public List<String> getVendorContains() {
        return vendorContains;
    }

    public void setVendorContains(List<String> vendorContains) {
        this.vendorContains = vendorContains != null ? vendorContains : new ArrayList<>();
    }

    public List<String> getHostnameContains() {
        return hostnameContains;
    }

    public void setHostnameContains(List<String> hostnameContains) {
        this.hostnameContains = hostnameContains != null ? hostnameContains : new ArrayList<>();
    }

    public List<String> getMdnsContains() {
        return mdnsContains;
    }

    public void setMdnsContains(List<String> mdnsContains) {
        this.mdnsContains = mdnsContains != null ? mdnsContains : new ArrayList<>();
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(int confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
