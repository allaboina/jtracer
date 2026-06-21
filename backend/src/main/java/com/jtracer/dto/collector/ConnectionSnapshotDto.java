package com.jtracer.dto.collector;

import com.jtracer.domain.enums.Confidence;
import com.jtracer.domain.enums.ConnectionDirection;
import com.jtracer.domain.enums.ConnectionState;
import com.jtracer.domain.enums.ProtocolType;

/**
 * Normalized network connection snapshot from a platform adapter before persistence.
 */
public class ConnectionSnapshotDto {

    private int pid;
    private String processName;
    private ProtocolType protocol;
    private String localIp;
    private Integer localPort;
    private String remoteIp;
    private Integer remotePort;
    private ConnectionState state;
    private ConnectionDirection direction;
    private String domain;
    private Confidence domainConfidence = Confidence.UNKNOWN;
    private Confidence confidence = Confidence.LIKELY;
    private String sourceAdapter;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Confidence getDomainConfidence() {
        return domainConfidence;
    }

    public void setDomainConfidence(Confidence domainConfidence) {
        this.domainConfidence = domainConfidence;
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
