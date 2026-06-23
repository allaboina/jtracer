package com.jtracer.collector.macos.parser;

import com.jtracer.domain.enums.Confidence;
import com.jtracer.domain.enums.EndpointType;
import com.jtracer.dto.collector.LanDeviceRawDto;
import com.jtracer.collector.common.EndpointClassifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses macOS {@code arp -a} output into LAN device snapshots.
 */
public final class MacArpParser {

    private static final Pattern ARP_ROW = Pattern.compile(
            "^(?:(\\S+) )?\\(([^)]+)\\) at ([0-9a-f:]+|\\(incomplete\\)) on (\\S+)");

    private MacArpParser() {
    }

    public static List<LanDeviceRawDto> parse(String arpOutput) {
        Map<String, LanDeviceRawDto> devices = new LinkedHashMap<>();
        if (arpOutput == null || arpOutput.isBlank()) {
            return List.of();
        }

        for (String line : arpOutput.split("\n")) {
            parseLine(line).ifPresent(device -> devices.put(deviceKey(device), device));
        }
        return new ArrayList<>(devices.values());
    }

    private static java.util.Optional<LanDeviceRawDto> parseLine(String line) {
        Matcher matcher = ARP_ROW.matcher(line.trim());
        if (!matcher.find()) {
            return java.util.Optional.empty();
        }

        String rawHostname = matcher.group(1);
        String ipAddress = matcher.group(2).trim();
        String rawMac = matcher.group(3).trim();
        String adapterName = matcher.group(4);

        if (!isLanCandidate(ipAddress)) {
            return java.util.Optional.empty();
        }
        if ("(incomplete)".equals(rawMac)) {
            return java.util.Optional.empty();
        }

        LanDeviceRawDto dto = new LanDeviceRawDto();
        dto.setIpAddress(ipAddress);
        dto.setMacAddress(normalizeMac(rawMac));
        dto.setAdapterName(adapterName);
        dto.setHostname(normalizeHostname(rawHostname));
        dto.setConfidence(resolveConfidence(dto));
        return java.util.Optional.of(dto);
    }

    static boolean isLanCandidate(String ipAddress) {
        EndpointType type = EndpointClassifier.classify(ipAddress);
        if (type != EndpointType.LAN_DEVICE) {
            return false;
        }
        if (ipAddress.endsWith(".255")) {
            return false;
        }
        return true;
    }

    static String normalizeHostname(String rawHostname) {
        if (rawHostname == null || rawHostname.isBlank() || "?".equals(rawHostname)) {
            return null;
        }
        return rawHostname;
    }

    static String normalizeMac(String mac) {
        return mac.toLowerCase(Locale.ROOT);
    }

    static Confidence resolveConfidence(LanDeviceRawDto dto) {
        if (dto.getMacAddress() != null && dto.getHostname() != null) {
            return Confidence.LIKELY;
        }
        if (dto.getMacAddress() != null) {
            return Confidence.LIKELY;
        }
        return Confidence.LOW;
    }

    private static String deviceKey(LanDeviceRawDto device) {
        if (device.getMacAddress() != null && !device.getMacAddress().isBlank()) {
            return "mac:" + device.getMacAddress();
        }
        return "ip:" + device.getIpAddress();
    }
}
