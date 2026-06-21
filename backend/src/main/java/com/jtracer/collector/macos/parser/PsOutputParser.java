package com.jtracer.collector.macos.parser;

import com.jtracer.dto.collector.ProcessMetricSnapshotDto;
import com.jtracer.dto.collector.ProcessSnapshotDto;
import com.jtracer.domain.enums.ProcessStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses macOS {@code ps} output produced by:
 * {@code ps -axo pid=,ppid=,pcpu=,pmem=,rss=,command=}
 */
public final class PsOutputParser {

    private static final Pattern LINE_PATTERN = Pattern.compile(
            "^\\s*(\\d+)\\s+(\\d+)\\s+([\\d.]+)\\s+([\\d.]+)\\s+(\\d+)\\s+(.+)$");

    private PsOutputParser() {
    }

    public static List<ProcessSnapshotDto> parseSnapshots(String psOutput) {
        List<ProcessSnapshotDto> snapshots = new ArrayList<>();
        for (String line : psOutput.split("\n")) {
            if (line.isBlank()) {
                continue;
            }
            Matcher matcher = LINE_PATTERN.matcher(line);
            if (!matcher.matches()) {
                continue;
            }
            String commandLine = matcher.group(6).trim();
            ProcessSnapshotDto dto = new ProcessSnapshotDto();
            dto.setPid(Integer.parseInt(matcher.group(1)));
            dto.setParentPid(Integer.parseInt(matcher.group(2)));
            dto.setCommandLine(commandLine);
            dto.setExecutablePath(extractExecutablePath(commandLine));
            dto.setProcessName(extractProcessName(commandLine));
            dto.setStatus(ProcessStatus.RUNNING);
            snapshots.add(dto);
        }
        return snapshots;
    }

    public static List<ProcessMetricSnapshotDto> parseMetrics(String psOutput) {
        List<ProcessMetricSnapshotDto> metrics = new ArrayList<>();
        for (String line : psOutput.split("\n")) {
            if (line.isBlank()) {
                continue;
            }
            Matcher matcher = LINE_PATTERN.matcher(line);
            if (!matcher.matches()) {
                continue;
            }
            ProcessMetricSnapshotDto dto = new ProcessMetricSnapshotDto();
            dto.setPid(Integer.parseInt(matcher.group(1)));
            dto.setCpuPct(new BigDecimal(matcher.group(3)));
            dto.setMemoryPct(new BigDecimal(matcher.group(4)));
            long rssKb = Long.parseLong(matcher.group(5));
            dto.setRssMb(BigDecimal.valueOf(rssKb).divide(BigDecimal.valueOf(1024), 2, RoundingMode.HALF_UP));
            metrics.add(dto);
        }
        return metrics;
    }

    static String extractExecutablePath(String commandLine) {
        if (commandLine == null || commandLine.isBlank()) {
            return null;
        }
        String trimmed = commandLine.trim();
        if (trimmed.startsWith("[")) {
            return trimmed;
        }
        if (trimmed.startsWith("/") && trimmed.contains(" (")) {
            return trimmed.substring(0, trimmed.indexOf(" ("));
        }
        if (trimmed.contains(".app/Contents/MacOS/")) {
            int flagIndex = trimmed.indexOf(" --");
            return flagIndex < 0 ? trimmed : trimmed.substring(0, flagIndex).trim();
        }
        int space = trimmed.indexOf(' ');
        return space < 0 ? trimmed : trimmed.substring(0, space);
    }

    static String extractProcessName(String commandLine) {
        String path = extractExecutablePath(commandLine);
        if (path == null || path.isBlank()) {
            return "unknown";
        }
        if (path.startsWith("[")) {
            return path;
        }
        if (path.contains(".app/Contents/MacOS/")) {
            int idx = path.indexOf("/Contents/MacOS/") + "/Contents/MacOS/".length();
            return path.substring(idx).trim();
        }
        int slash = path.lastIndexOf('/');
        return slash >= 0 ? path.substring(slash + 1) : path;
    }
}
