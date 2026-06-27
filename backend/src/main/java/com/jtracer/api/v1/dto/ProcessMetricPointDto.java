package com.jtracer.api.v1.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class ProcessMetricPointDto {

    private Instant timestamp;
    private BigDecimal cpuPct;
    private BigDecimal memoryPct;
    private BigDecimal rssMb;

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

    public BigDecimal getRssMb() {
        return rssMb;
    }

    public void setRssMb(BigDecimal rssMb) {
        this.rssMb = rssMb;
    }
}
