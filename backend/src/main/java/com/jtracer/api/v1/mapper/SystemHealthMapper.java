package com.jtracer.api.v1.mapper;

import com.jtracer.api.v1.dto.SystemHealthDataDto;
import com.jtracer.domain.entity.SystemHealthSnapshot;
import org.springframework.stereotype.Component;

@Component
public class SystemHealthMapper {

    public SystemHealthDataDto toDto(SystemHealthSnapshot snapshot) {
        SystemHealthDataDto dto = new SystemHealthDataDto();
        dto.setCpuPct(snapshot.getTotalCpuPct());
        dto.setMemoryPct(snapshot.getTotalMemoryPct());
        dto.setUsedMemoryMb(snapshot.getUsedMemoryMb());
        dto.setTotalMemoryMb(snapshot.getTotalMemoryMb());
        dto.setDiskUsagePct(snapshot.getDiskUsagePct());
        dto.setBatteryPct(snapshot.getBatteryPct());
        dto.setBatteryCharging(snapshot.getBatteryCharging());
        dto.setActiveProcessCount(snapshot.getActiveProcessCount());
        dto.setActiveConnectionCount(snapshot.getActiveConnectionCount());
        dto.setOnlineLanDeviceCount(snapshot.getOnlineLanDeviceCount());
        return dto;
    }
}
