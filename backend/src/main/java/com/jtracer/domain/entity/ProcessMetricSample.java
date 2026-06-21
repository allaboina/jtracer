package com.jtracer.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "process_metric_samples")
public class ProcessMetricSample {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "observed_process_id", nullable = false)
    private ObservedProcess observedProcess;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private ObservationSession session;

    @Column(name = "sampled_at", nullable = false)
    private Instant sampledAt;

    @Column(name = "cpu_pct", precision = 8, scale = 2)
    private BigDecimal cpuPct;

    @Column(name = "memory_pct", precision = 8, scale = 2)
    private BigDecimal memoryPct;

    @Column(name = "rss_mb", precision = 12, scale = 2)
    private BigDecimal rssMb;

    @Column(name = "thread_count")
    private Integer threadCount;

    @Column(name = "open_file_count")
    private Integer openFileCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ObservedProcess getObservedProcess() {
        return observedProcess;
    }

    public void setObservedProcess(ObservedProcess observedProcess) {
        this.observedProcess = observedProcess;
    }

    public ObservationSession getSession() {
        return session;
    }

    public void setSession(ObservationSession session) {
        this.session = session;
    }

    public Instant getSampledAt() {
        return sampledAt;
    }

    public void setSampledAt(Instant sampledAt) {
        this.sampledAt = sampledAt;
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

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    public Integer getOpenFileCount() {
        return openFileCount;
    }

    public void setOpenFileCount(Integer openFileCount) {
        this.openFileCount = openFileCount;
    }
}
