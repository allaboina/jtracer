package com.jtracer.domain.entity;

import com.jtracer.domain.enums.ScanStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "network_scan_sessions")
public class NetworkScanSession {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "observation_session_id", nullable = false)
    private ObservationSession observationSession;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "subnet_cidr")
    private String subnetCidr;

    @Column(name = "adapter_name")
    private String adapterName;

    @Column(name = "scanned_host_count")
    private Integer scannedHostCount;

    @Column(name = "online_device_count")
    private Integer onlineDeviceCount;

    @Column(name = "unknown_device_count")
    private Integer unknownDeviceCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScanStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ObservationSession getObservationSession() {
        return observationSession;
    }

    public void setObservationSession(ObservationSession observationSession) {
        this.observationSession = observationSession;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public String getSubnetCidr() {
        return subnetCidr;
    }

    public void setSubnetCidr(String subnetCidr) {
        this.subnetCidr = subnetCidr;
    }

    public String getAdapterName() {
        return adapterName;
    }

    public void setAdapterName(String adapterName) {
        this.adapterName = adapterName;
    }

    public Integer getScannedHostCount() {
        return scannedHostCount;
    }

    public void setScannedHostCount(Integer scannedHostCount) {
        this.scannedHostCount = scannedHostCount;
    }

    public Integer getOnlineDeviceCount() {
        return onlineDeviceCount;
    }

    public void setOnlineDeviceCount(Integer onlineDeviceCount) {
        this.onlineDeviceCount = onlineDeviceCount;
    }

    public Integer getUnknownDeviceCount() {
        return unknownDeviceCount;
    }

    public void setUnknownDeviceCount(Integer unknownDeviceCount) {
        this.unknownDeviceCount = unknownDeviceCount;
    }

    public ScanStatus getStatus() {
        return status;
    }

    public void setStatus(ScanStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
