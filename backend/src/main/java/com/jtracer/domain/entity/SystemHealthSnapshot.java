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
@Table(name = "system_health_snapshots")
public class SystemHealthSnapshot {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private ObservationSession session;

    @Column(name = "sampled_at", nullable = false)
    private Instant sampledAt;

    @Column(name = "total_cpu_pct", precision = 8, scale = 2)
    private BigDecimal totalCpuPct;

    @Column(name = "total_memory_pct", precision = 8, scale = 2)
    private BigDecimal totalMemoryPct;

    @Column(name = "used_memory_mb", precision = 12, scale = 2)
    private BigDecimal usedMemoryMb;

    @Column(name = "total_memory_mb", precision = 12, scale = 2)
    private BigDecimal totalMemoryMb;

    @Column(name = "disk_usage_pct", precision = 8, scale = 2)
    private BigDecimal diskUsagePct;

    @Column(name = "battery_pct", precision = 8, scale = 2)
    private BigDecimal batteryPct;

    @Column(name = "battery_charging")
    private Boolean batteryCharging;

    @Column(name = "active_process_count")
    private Integer activeProcessCount;

    @Column(name = "active_connection_count")
    private Integer activeConnectionCount;

    @Column(name = "online_lan_device_count")
    private Integer onlineLanDeviceCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
