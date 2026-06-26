package com.jtracer.dto.collector;

import com.jtracer.domain.enums.Confidence;
import com.jtracer.domain.enums.DeviceType;
import com.jtracer.domain.enums.IdentitySource;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolved device identity output from the identity resolver.
 */
public class DeviceIdentityResultDto {

    private String displayName;
    private DeviceType deviceType = DeviceType.UNKNOWN;
    private String manufacturer;
    private Confidence confidence = Confidence.UNKNOWN;
    private IdentitySource identitySource = IdentitySource.COMBINED_RULE;
    private int confidenceScore;
    private final List<String> evidence = new ArrayList<>();

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public void setConfidence(Confidence confidence) {
        this.confidence = confidence;
    }

    public IdentitySource getIdentitySource() {
        return identitySource;
    }

    public void setIdentitySource(IdentitySource identitySource) {
        this.identitySource = identitySource;
    }

    public int getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(int confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public void addEvidence(String line) {
        evidence.add(line);
    }

    public String evidenceSummary() {
        return String.join("; ", evidence);
    }
}
