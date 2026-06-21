package com.jtracer.dto.collector;

import java.math.BigDecimal;

/**
 * Normalized process metric snapshot from a platform adapter before persistence.
 */
public class ProcessMetricSnapshotDto {

    private int pid;
    private BigDecimal cpuPct;
    private BigDecimal memoryPct;
    private BigDecimal rssMb;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
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

    public BigDecimal getRssMb() {
        return rssMb;
    }

    public void setRssMb(BigDecimal rssMb) {
        this.rssMb = rssMb;
    }
}
