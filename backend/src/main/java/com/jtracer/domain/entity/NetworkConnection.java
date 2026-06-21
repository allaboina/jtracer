package com.jtracer.domain.entity;

import com.jtracer.domain.enums.Confidence;
import com.jtracer.domain.enums.ConnectionDirection;
import com.jtracer.domain.enums.ConnectionState;
import com.jtracer.domain.enums.ProtocolType;
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
@Table(name = "network_connections")
public class NetworkConnection {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private ObservationSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "observed_process_id")
    private ObservedProcess observedProcess;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProtocolType protocol;

    @Column(name = "local_ip")
    private String localIp;

    @Column(name = "local_port")
    private Integer localPort;

    @Column(name = "remote_ip")
    private String remoteIp;

    @Column(name = "remote_port")
    private Integer remotePort;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remote_endpoint_id")
    private RemoteEndpoint remoteEndpoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_identity_id")
    private DomainIdentity domainIdentity;

    @Column(name = "first_seen_at", nullable = false)
    private Instant firstSeenAt;

    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectionState state;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectionDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Confidence confidence;

    @Column(name = "source_adapter")
    private String sourceAdapter;

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

    public ObservedProcess getObservedProcess() {
        return observedProcess;
    }

    public void setObservedProcess(ObservedProcess observedProcess) {
        this.observedProcess = observedProcess;
    }

    public ProtocolType getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolType protocol) {
        this.protocol = protocol;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public Integer getLocalPort() {
        return localPort;
    }

    public void setLocalPort(Integer localPort) {
        this.localPort = localPort;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public Integer getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(Integer remotePort) {
        this.remotePort = remotePort;
    }

    public RemoteEndpoint getRemoteEndpoint() {
        return remoteEndpoint;
    }

    public void setRemoteEndpoint(RemoteEndpoint remoteEndpoint) {
        this.remoteEndpoint = remoteEndpoint;
    }

    public DomainIdentity getDomainIdentity() {
        return domainIdentity;
    }

    public void setDomainIdentity(DomainIdentity domainIdentity) {
        this.domainIdentity = domainIdentity;
    }

    public Instant getFirstSeenAt() {
        return firstSeenAt;
    }

    public void setFirstSeenAt(Instant firstSeenAt) {
        this.firstSeenAt = firstSeenAt;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public ConnectionState getState() {
        return state;
    }

    public void setState(ConnectionState state) {
        this.state = state;
    }

    public ConnectionDirection getDirection() {
        return direction;
    }

    public void setDirection(ConnectionDirection direction) {
        this.direction = direction;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public void setConfidence(Confidence confidence) {
        this.confidence = confidence;
    }

    public String getSourceAdapter() {
        return sourceAdapter;
    }

    public void setSourceAdapter(String sourceAdapter) {
        this.sourceAdapter = sourceAdapter;
    }
}
