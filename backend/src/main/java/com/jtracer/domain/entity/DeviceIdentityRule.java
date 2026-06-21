package com.jtracer.domain.entity;

import com.jtracer.domain.enums.Confidence;
import com.jtracer.domain.enums.DeviceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "device_identity_rules")
public class DeviceIdentityRule {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(name = "rule_name", nullable = false)
    private String ruleName;

    @Column(name = "vendor_contains", length = 512)
    private String vendorContains;

    @Column(name = "hostname_contains", length = 1024)
    private String hostnameContains;

    @Column(name = "mac_oui_prefix")
    private String macOuiPrefix;

    @Column(name = "mdns_contains", length = 1024)
    private String mdnsContains;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    private DeviceType deviceType;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Confidence confidence;

    @Column(nullable = false)
    private Integer priority;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getVendorContains() {
        return vendorContains;
    }

    public void setVendorContains(String vendorContains) {
        this.vendorContains = vendorContains;
    }

    public String getHostnameContains() {
        return hostnameContains;
    }

    public void setHostnameContains(String hostnameContains) {
        this.hostnameContains = hostnameContains;
    }

    public String getMacOuiPrefix() {
        return macOuiPrefix;
    }

    public void setMacOuiPrefix(String macOuiPrefix) {
        this.macOuiPrefix = macOuiPrefix;
    }

    public String getMdnsContains() {
        return mdnsContains;
    }

    public void setMdnsContains(String mdnsContains) {
        this.mdnsContains = mdnsContains;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public void setConfidence(Confidence confidence) {
        this.confidence = confidence;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
