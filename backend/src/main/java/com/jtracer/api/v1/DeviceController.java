package com.jtracer.api.v1;

import com.jtracer.api.ResourceNotFoundException;
import com.jtracer.api.common.ApiResponse;
import com.jtracer.api.v1.dto.DeviceDetailDto;
import com.jtracer.api.v1.dto.DeviceLabelRequestDto;
import com.jtracer.api.v1.dto.DeviceSummaryDto;
import com.jtracer.service.DeviceQueryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

    private final DeviceQueryService deviceQueryService;

    public DeviceController(DeviceQueryService deviceQueryService) {
        this.deviceQueryService = deviceQueryService;
    }

    @GetMapping
    public ApiResponse<List<DeviceSummaryDto>> listDevices(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean unknown) {
        return ApiResponse.ok(deviceQueryService.listDevices(status, type, unknown));
    }

    @GetMapping("/{deviceId}")
    public ApiResponse<DeviceDetailDto> getDevice(@PathVariable String deviceId) {
        DeviceDetailDto device = deviceQueryService
                .getDevice(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device", deviceId));
        return ApiResponse.ok(device);
    }

    @PostMapping("/{deviceId}/label")
    public ApiResponse<DeviceDetailDto> labelDevice(
            @PathVariable String deviceId, @Valid @RequestBody DeviceLabelRequestDto request) {
        return ApiResponse.ok(deviceQueryService.applyLabel(deviceId, request.getLabel(), request.getDeviceType()));
    }
}
