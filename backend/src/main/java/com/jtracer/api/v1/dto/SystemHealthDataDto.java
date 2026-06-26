package com.jtracer.api.v1.dto;

import java.math.BigDecimal;

public class SystemHealthDataDto {

    private BigDecimal cpuPct;
    private BigDecimal memoryPct;
    private BigDecimal usedMemoryMb;
    private BigDecimal totalMemoryMb;
    private BigDecimal diskUsagePct;
    private BigDecimal batteryPct;
    private Boolean batteryCharging;
    private Integer activeProcessCount;
    private Integer activeConnectionCount;
    private Integer onlineLanDeviceCount;

    public BigDecimal getCpuPct() {
        return cpuPct;
    }

    public void setCpuPct(BigDecimal cpuPct) {
        this.cpuPct = cpuPct;
    }

    public BigDecimal getMemoryPct() {
        return memoryPct;
    }

    public void setMemoryPct(BigDecimal memoryPct) {
        this.memoryPct = memoryPct;
    }

    public BigDecimal getUsedMemoryMb() {
        return usedMemoryMb;
    }

    public void setUsedMemoryMb(BigDecimal usedMemoryMb) {
        this.usedMemoryMb = usedMemoryMb;
    }

    public BigDecimal getTotalMemoryMb() {
        return totalMemoryMb;
    }

    public void setTotalMemoryMb(BigDecimal totalMemoryMb) {
        this.totalMemoryMb = totalMemoryMb;
    }

    public BigDecimal getDiskUsagePct() {
        return diskUsagePct;
    }

    public void setDiskUsagePct(BigDecimal diskUsagePct) {
        this.diskUsagePct = diskUsagePct;
    }

    public BigDecimal getBatteryPct() {
        return batteryPct;
    }

    public void setBatteryPct(BigDecimal batteryPct) {
        this.batteryPct = batteryPct;
    }

    public Boolean getBatteryCharging() {
        return batteryCharging;
    }

    public void setBatteryCharging(Boolean batteryCharging) {
        this.batteryCharging = batteryCharging;
    }

    public Integer getActiveProcessCount() {
        return activeProcessCount;
    }

    public void setActiveProcessCount(Integer activeProcessCount) {
        this.activeProcessCount = activeProcessCount;
    }

    public Integer getActiveConnectionCount() {
        return activeConnectionCount;
    }

    public void setActiveConnectionCount(Integer activeConnectionCount) {
        this.activeConnectionCount = activeConnectionCount;
    }

    public Integer getOnlineLanDeviceCount() {
        return onlineLanDeviceCount;
    }

    public void setOnlineLanDeviceCount(Integer onlineLanDeviceCount) {
        this.onlineLanDeviceCount = onlineLanDeviceCount;
    }
}
