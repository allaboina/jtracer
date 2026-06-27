package com.jtracer.api.v1.mapper;

import com.jtracer.api.v1.dto.ConnectionSummaryDto;
import com.jtracer.api.v1.dto.DomainSummaryDto;
import com.jtracer.api.v1.dto.ProcessConnectionDto;
import com.jtracer.api.v1.dto.ProcessDetailDto;
import com.jtracer.api.v1.dto.ProcessMetricPointDto;
import com.jtracer.api.v1.dto.ProcessSummaryDto;
import com.jtracer.domain.entity.DomainIdentity;
import com.jtracer.domain.entity.LanDevice;
import com.jtracer.domain.entity.NetworkConnection;
import com.jtracer.domain.entity.ObservedProcess;
import com.jtracer.domain.entity.ProcessMetricSample;
import com.jtracer.api.v1.dto.DeviceDetailDto;
import com.jtracer.api.v1.dto.DeviceSummaryDto;
import org.springframework.stereotype.Component;

@Component
public class ApiMapper {

    public ProcessSummaryDto toProcessSummary(
            ObservedProcess process, ProcessMetricSample latestMetric, int connectionCount) {
        ProcessSummaryDto dto = new ProcessSummaryDto();
        dto.setProcessId(process.getId());
        dto.setPid(process.getPid());
        dto.setProcessName(process.getProcessName());
        dto.setStatus(process.getStatus());
        dto.setConnectionCount(connectionCount);
        if (latestMetric != null) {
            dto.setCpuPct(latestMetric.getCpuPct());
            dto.setMemoryPct(latestMetric.getMemoryPct());
            dto.setRssMb(latestMetric.getRssMb());
        }
        return dto;
    }

    public ProcessDetailDto toProcessDetail(ObservedProcess process) {
        ProcessDetailDto dto = new ProcessDetailDto();
        dto.setProcessId(process.getId());
        dto.setPid(process.getPid());
        dto.setProcessName(process.getProcessName());
        dto.setCommandLine(process.getCommandLine());
        dto.setExecutablePath(process.getExecutablePath());
        dto.setAppName(process.getAppName());
        dto.setFirstSeenAt(process.getFirstSeenAt());
        dto.setLastSeenAt(process.getLastSeenAt());
        dto.setStatus(process.getStatus());
        return dto;
    }

    public ProcessMetricPointDto toProcessMetricPoint(ProcessMetricSample sample) {
        ProcessMetricPointDto dto = new ProcessMetricPointDto();
        dto.setTimestamp(sample.getSampledAt());
        dto.setCpuPct(sample.getCpuPct());
        dto.setMemoryPct(sample.getMemoryPct());
        dto.setRssMb(sample.getRssMb());
        return dto;
    }

    public ConnectionSummaryDto toConnectionSummary(NetworkConnection connection) {
        ConnectionSummaryDto dto = new ConnectionSummaryDto();
        dto.setConnectionId(connection.getId());
        dto.setProtocol(connection.getProtocol());
        dto.setLocalIp(connection.getLocalIp());
        dto.setLocalPort(connection.getLocalPort());
        dto.setRemoteIp(connection.getRemoteIp());
        dto.setRemotePort(connection.getRemotePort());
        dto.setState(connection.getState());
        dto.setDomain(resolveDomain(connection.getDomainIdentity()));
        if (connection.getObservedProcess() != null) {
            dto.setProcessId(connection.getObservedProcess().getId());
            dto.setProcessName(connection.getObservedProcess().getProcessName());
        }
        return dto;
    }

    public ProcessConnectionDto toProcessConnection(NetworkConnection connection) {
        ProcessConnectionDto dto = new ProcessConnectionDto();
        dto.setRemoteIp(connection.getRemoteIp());
        dto.setPort(connection.getRemotePort());
        dto.setProtocol(connection.getProtocol());
        dto.setDomain(resolveDomain(connection.getDomainIdentity()));
        return dto;
    }

    public DomainSummaryDto toDomainSummary(
            String domain, int connectionCount, int processCount, java.time.Instant lastSeenAt) {
        DomainSummaryDto dto = new DomainSummaryDto();
        dto.setDomain(domain);
        dto.setConnectionCount(connectionCount);
        dto.setProcessCount(processCount);
        dto.setLastSeenAt(lastSeenAt);
        return dto;
    }

    public DeviceSummaryDto toDeviceSummary(LanDevice device) {
        DeviceSummaryDto dto = new DeviceSummaryDto();
        dto.setDeviceId(device.getId());
        dto.setIpAddress(device.getIpAddress());
        dto.setMacAddress(device.getMacAddress());
        dto.setVendor(device.getVendor());
        dto.setDeviceType(device.getDeviceType());
        dto.setConfidence(device.getConfidence());
        dto.setStatus(device.getStatus());
        dto.setDisplayName(resolveDeviceDisplayName(device));
        return dto;
    }

    public DeviceDetailDto toDeviceDetail(LanDevice device) {
        DeviceDetailDto dto = new DeviceDetailDto();
        dto.setDeviceId(device.getId());
        dto.setIpAddress(device.getIpAddress());
        dto.setMacAddress(device.getMacAddress());
        dto.setHostname(device.getHostname());
        dto.setVendor(device.getVendor());
        dto.setDeviceType(device.getDeviceType());
        dto.setConfidence(device.getConfidence());
        dto.setStatus(device.getStatus());
        dto.setFirstSeenAt(device.getFirstSeenAt());
        dto.setLastSeenAt(device.getLastSeenAt());
        dto.setDisplayName(resolveDeviceDisplayName(device));
        if (device.getDeviceIdentity() != null) {
            dto.setEvidence(device.getDeviceIdentity().getEvidence());
        }
        return dto;
    }

    private String resolveDomain(DomainIdentity domainIdentity) {
        if (domainIdentity == null) {
            return null;
        }
        if (domainIdentity.getRootDomain() != null && !domainIdentity.getRootDomain().isBlank()) {
            return domainIdentity.getRootDomain();
        }
        return domainIdentity.getHostname();
    }

    private String resolveDeviceDisplayName(LanDevice device) {
        if (device.getDeviceIdentity() != null
                && device.getDeviceIdentity().getDisplayName() != null) {
            return device.getDeviceIdentity().getDisplayName();
        }
        if (device.getHostname() != null && !device.getHostname().isBlank()) {
            return device.getHostname();
        }
        return "Unknown Device";
    }
}
