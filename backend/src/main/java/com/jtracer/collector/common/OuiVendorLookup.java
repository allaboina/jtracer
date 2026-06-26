package com.jtracer.collector.common;

import java.util.Locale;
import java.util.Map;

/**
 * Looks up IEEE OUI vendor names from a local prefix map.
 */
public final class OuiVendorLookup {

    private final Map<String, String> ouiToVendor;

    public OuiVendorLookup(Map<String, String> ouiToVendor) {
        this.ouiToVendor = ouiToVendor != null ? Map.copyOf(ouiToVendor) : Map.of();
    }

    public String lookupVendor(String macAddress) {
        String prefix = extractOuiPrefix(macAddress);
        if (prefix == null) {
            return null;
        }
        return ouiToVendor.get(prefix);
    }

    public static String extractOuiPrefix(String macAddress) {
        if (macAddress == null || macAddress.isBlank()) {
            return null;
        }
        String[] parts = macAddress.trim().toLowerCase(Locale.ROOT).split("[:\\-.]");
        if (parts.length < 3) {
            return null;
        }
        return parts[0] + ":" + parts[1] + ":" + parts[2];
    }
}
