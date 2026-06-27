package com.jtracer.service;

import com.jtracer.api.v1.dto.DeviceDetailDto;
import com.jtracer.api.v1.dto.DeviceSummaryDto;
import com.jtracer.domain.enums.DeviceType;
import java.util.List;
import java.util.Optional;

public interface DeviceQueryService {

    List<DeviceSummaryDto> listDevices(String status, String type, Boolean unknown);

    Optional<DeviceDetailDto> getDevice(String deviceId);

    DeviceDetailDto applyLabel(String deviceId, String label, DeviceType deviceType);
}
