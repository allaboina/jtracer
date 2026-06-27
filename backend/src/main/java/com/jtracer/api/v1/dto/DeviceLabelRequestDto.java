package com.jtracer.api.v1.dto;

import com.jtracer.domain.enums.DeviceType;
import jakarta.validation.constraints.NotBlank;

public class DeviceLabelRequestDto {

    @NotBlank
    private String label;

    private DeviceType deviceType;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }
}
