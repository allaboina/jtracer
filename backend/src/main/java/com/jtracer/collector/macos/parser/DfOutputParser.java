package com.jtracer.collector.macos.parser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses macOS {@code df -k /} output for root filesystem usage.
 */
public final class DfOutputParser {

    private static final Pattern DATA_LINE = Pattern.compile(
            "^\\S+\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)%");

    private DfOutputParser() {
    }

    public static BigDecimal parseDiskUsagePercent(String dfOutput) {
        if (dfOutput == null) {
            return null;
        }
        for (String line : dfOutput.split("\n")) {
            Matcher matcher = DATA_LINE.matcher(line.trim());
            if (matcher.find()) {
                return new BigDecimal(matcher.group(4));
            }
        }
        long totalKb = 0;
        long usedKb = 0;
        for (String line : dfOutput.split("\n")) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length >= 5 && parts[0].startsWith("/")) {
                try {
                    totalKb = Long.parseLong(parts[1]);
                    usedKb = Long.parseLong(parts[2]);
                } catch (NumberFormatException ignored) {
                    continue;
                }
            }
        }
        if (totalKb <= 0) {
            return null;
        }
        return BigDecimal.valueOf(usedKb)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalKb), 1, RoundingMode.HALF_UP);
    }
}
