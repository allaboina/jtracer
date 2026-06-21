package com.jtracer.collector.macos.parser;

import com.jtracer.domain.enums.ConnectionDirection;
import com.jtracer.domain.enums.ConnectionState;
import com.jtracer.domain.enums.ProtocolType;
import com.jtracer.dto.collector.ConnectionSnapshotDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses macOS {@code lsof -nP -iTCP -iUDP} output into connection snapshots.
 */
public final class LsofOutputParser {

    private static final Pattern ROW_PATTERN = Pattern.compile(
            "^(\\S+)\\s+(\\d+)\\s+\\S+\\s+\\S+\\s+\\S+\\s+\\S+\\s+\\S+\\s+(.+)$");

    private static final Pattern TCP_CONNECTION = Pattern.compile(
            "^(TCP|UDP)\\s+"
                    + "(.+?):(\\d+|\\*)"
                    + "(?:->(.+?):(\\d+|\\*))?"
                    + "(?:\\s+\\(([^)]+)\\))?$");

    private LsofOutputParser() {
    }

    public static List<ConnectionSnapshotDto> parse(String lsofOutput) {
        List<ConnectionSnapshotDto> connections = new ArrayList<>();
        if (lsofOutput == null || lsofOutput.isBlank()) {
            return connections;
        }

        for (String line : lsofOutput.split("\n")) {
            if (line.isBlank() || line.startsWith("COMMAND")) {
                continue;
            }
            parseLine(line).ifPresent(connections::add);
        }
        return connections;
    }

    private static java.util.Optional<ConnectionSnapshotDto> parseLine(String line) {
        Matcher row = ROW_PATTERN.matcher(line.trim());
        if (!row.matches()) {
            return java.util.Optional.empty();
        }

        String processName = row.group(1);
        int pid = Integer.parseInt(row.group(2));
        String nameField = row.group(3).trim();

        Matcher connection = TCP_CONNECTION.matcher(nameField);
        if (!connection.matches()) {
            return java.util.Optional.empty();
        }

        ProtocolType protocol = ProtocolType.valueOf(connection.group(1));
        String localIp = normalizeAddress(connection.group(2));
        Integer localPort = parsePort(connection.group(3));
        String remoteIp = normalizeAddress(connection.group(4));
        Integer remotePort = parsePort(connection.group(5));
        String rawState = connection.group(6);

        if (remoteIp == null || remotePort == null) {
            return java.util.Optional.empty();
        }

        ConnectionState state = mapState(rawState);
        ConnectionDirection direction = mapDirection(localIp, state);

        ConnectionSnapshotDto dto = new ConnectionSnapshotDto();
        dto.setPid(pid);
        dto.setProcessName(processName);
        dto.setProtocol(protocol);
        dto.setLocalIp(localIp);
        dto.setLocalPort(localPort);
        dto.setRemoteIp(remoteIp);
        dto.setRemotePort(remotePort);
        dto.setState(state);
        dto.setDirection(direction);
        dto.setSourceAdapter("macos-lsof");
        return java.util.Optional.of(dto);
    }

    static ConnectionState mapState(String rawState) {
        if (rawState == null || rawState.isBlank()) {
            return ConnectionState.UNKNOWN;
        }
        return switch (rawState.toUpperCase(Locale.ROOT)) {
            case "ESTABLISHED", "SYN_SENT", "SYN_RECV" -> ConnectionState.ACTIVE;
            case "LISTEN" -> ConnectionState.LISTENING;
            case "CLOSE_WAIT", "TIME_WAIT", "CLOSED", "FIN_WAIT_1", "FIN_WAIT_2", "LAST_ACK" -> ConnectionState.CLOSED;
            default -> ConnectionState.UNKNOWN;
        };
    }

    static ConnectionDirection mapDirection(String localIp, ConnectionState state) {
        if (state == ConnectionState.LISTENING) {
            return ConnectionDirection.INBOUND;
        }
        if ("127.0.0.1".equals(localIp) || "::1".equals(localIp)) {
            return ConnectionDirection.LOCAL;
        }
        return ConnectionDirection.OUTBOUND;
    }

    static String normalizeAddress(String address) {
        if (address == null || address.isBlank() || "*".equals(address)) {
            return null;
        }
        if (address.startsWith("[") && address.endsWith("]")) {
            return address.substring(1, address.length() - 1);
        }
        return address;
    }

    static Integer parsePort(String port) {
        if (port == null || port.isBlank() || "*".equals(port)) {
            return null;
        }
        return Integer.parseInt(port);
    }
}
