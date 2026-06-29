package com.jtracer.api.v1.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class SystemHealthSnapshotPointDto {

    private Instant timestamp;
    private BigDecimal cpuPct;
    private BigDecimal memoryPct;
    private BigDecimal diskUsagePct;
    private Integer activeProcessCount;
    private Integer activeConnectionCount;

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

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

    public BigDecimal getDiskUsagePct() {
        return diskUsagePct;
    }

    public void setDiskUsagePct(BigDecimal diskUsagePct) {
        this.diskUsagePct = diskUsagePct;
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
}
