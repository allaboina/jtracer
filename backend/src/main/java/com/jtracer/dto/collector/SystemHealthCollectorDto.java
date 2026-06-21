package com.jtracer.dto.collector;

import java.math.BigDecimal;

/**
 * Normalized system health snapshot from a platform adapter before persistence.
 */
public class SystemHealthCollectorDto {

    private BigDecimal totalCpuPct;
    private BigDecimal totalMemoryPct;
    private BigDecimal usedMemoryMb;
    private BigDecimal totalMemoryMb;
    private BigDecimal diskUsagePct;
    private BigDecimal batteryPct;
    private Boolean batteryCharging;
    private Integer activeProcessCount;

    public BigDecimal getTotalCpuPct() {
        return totalCpuPct;
    }

    public void setTotalCpuPct(BigDecimal totalCpuPct) {
        this.totalCpuPct = totalCpuPct;
    }

    public BigDecimal getTotalMemoryPct() {
        return totalMemoryPct;
    }

    public void setTotalMemoryPct(BigDecimal totalMemoryPct) {
        this.totalMemoryPct = totalMemoryPct;
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
}
