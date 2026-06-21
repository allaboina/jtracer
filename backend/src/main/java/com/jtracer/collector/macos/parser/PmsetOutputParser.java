package com.jtracer.collector.macos.parser;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses macOS {@code pmset -g batt} output.
 */
public final class PmsetOutputParser {

    private static final Pattern BATTERY_LINE = Pattern.compile(
            "(-InternalBattery[^\\n]*?\\t)(\\d+)%\\s*;\\s*([^;]+);");

    private PmsetOutputParser() {
    }

    public record BatteryStatus(BigDecimal percentage, Boolean charging) {
    }

    public static BatteryStatus parseBatteryStatus(String pmsetOutput) {
        if (pmsetOutput == null || pmsetOutput.isBlank()) {
            return null;
        }
        Matcher matcher = BATTERY_LINE.matcher(pmsetOutput);
        if (!matcher.find()) {
            return null;
        }
        BigDecimal pct = new BigDecimal(matcher.group(2));
        String state = matcher.group(3).trim().toLowerCase();
        boolean charging = state.contains("charging") && !state.contains("not charging");
        return new BatteryStatus(pct, charging);
    }
}
