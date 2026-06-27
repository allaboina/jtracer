package com.jtracer.api.v1.dto;

import com.jtracer.domain.enums.ProcessStatus;
import java.math.BigDecimal;

public class ProcessSummaryDto {

    private String processId;
    private Integer pid;
    private String processName;
    private BigDecimal cpuPct;
    private BigDecimal memoryPct;
    private BigDecimal rssMb;
    private Integer connectionCount;
    private ProcessStatus status;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
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

    public Integer getConnectionCount() {
        return connectionCount;
    }

    public void setConnectionCount(Integer connectionCount) {
        this.connectionCount = connectionCount;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }
}
