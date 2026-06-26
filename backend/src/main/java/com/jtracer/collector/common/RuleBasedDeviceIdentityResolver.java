package com.jtracer.collector.common;

import com.jtracer.domain.enums.Confidence;
import com.jtracer.domain.enums.DeviceType;
import com.jtracer.domain.enums.IdentitySource;
import com.jtracer.dto.collector.DeviceIdentityResultDto;
import com.jtracer.dto.collector.LanDeviceRawDto;
import java.util.Comparator;
import java.util.Locale;

/**
 * Rule-based device identity resolver using local OUI data and bundled rules.
 */
public class RuleBasedDeviceIdentityResolver implements DeviceIdentityResolver {

    private final DeviceIdentityKnowledgeBase knowledgeBase;
    private final OuiVendorLookup ouiLookup;

    public RuleBasedDeviceIdentityResolver(DeviceIdentityKnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
        this.ouiLookup = new OuiVendorLookup(knowledgeBase.getOuiVendors());
    }

    @Override
    public DeviceIdentityResultDto resolveIdentity(LanDeviceRawDto rawDevice) {
        return resolveIdentity(rawDevice, null);
    }

    @Override
    public DeviceIdentityResultDto resolveIdentity(LanDeviceRawDto rawDevice, String userLabel) {
        DeviceIdentityResultDto result = new DeviceIdentityResultDto();

        if (userLabel != null && !userLabel.isBlank()) {
            result.setDisplayName(userLabel.trim());
            result.setDeviceType(DeviceType.UNKNOWN);
            result.setConfidence(Confidence.CONFIRMED);
            result.setConfidenceScore(100);
            result.setIdentitySource(IdentitySource.USER_LABEL);
            result.addEvidence("User label override");
            return result;
        }

        String vendor = resolveVendor(rawDevice);
        result.setManufacturer(vendor);
        if (vendor != null) {
            result.addEvidence("Vendor = " + vendor);
        }

        String hostname = normalizeSignal(rawDevice.getHostname());
        String mdns = normalizeSignal(rawDevice.getMdnsName());

        DeviceIdentityResultDto mdnsMatch = matchMdnsService(mdns);
        if (mdnsMatch != null) {
            mergeStronger(result, mdnsMatch);
        }

        DeviceIdentityResultDto ruleMatch = matchRules(vendor, hostname, mdns);
        if (ruleMatch != null) {
            mergeStronger(result, ruleMatch);
        }

        if (result.getDisplayName() == null && vendor != null) {
            result.setDisplayName(vendor + " Device");
            result.setDeviceType(DeviceType.UNKNOWN);
            result.setConfidence(Confidence.LOW);
            result.setConfidenceScore(50);
            result.setIdentitySource(IdentitySource.OUI_RULE);
            result.addEvidence("Vendor-only OUI lookup");
        }

        if (result.getDisplayName() == null) {
            result.setDisplayName("Unknown Device");
            result.setDeviceType(DeviceType.UNKNOWN);
            result.setConfidence(Confidence.UNKNOWN);
            result.setConfidenceScore(0);
            result.setIdentitySource(IdentitySource.COMBINED_RULE);
            result.addEvidence("No matching identity signals");
        }

        return result;
    }

    private String resolveVendor(LanDeviceRawDto rawDevice) {
        if (rawDevice.getVendor() != null && !rawDevice.getVendor().isBlank()) {
            return rawDevice.getVendor().trim();
        }
        return ouiLookup.lookupVendor(rawDevice.getMacAddress());
    }

    private DeviceIdentityResultDto matchMdnsService(String mdns) {
        if (mdns == null) {
            return null;
        }
        String lowerMdns = mdns.toLowerCase(Locale.ROOT);
        return knowledgeBase.getMdnsServices().stream()
                .filter(service -> lowerMdns.contains(service.getServiceName().toLowerCase(Locale.ROOT)))
                .max(Comparator.comparingInt(MdnsServiceDefinition::getConfidenceScore))
                .map(service -> {
                    DeviceIdentityResultDto dto = new DeviceIdentityResultDto();
                    dto.setDisplayName(service.getDisplayName());
                    dto.setDeviceType(parseDeviceType(service.getDeviceType()));
                    dto.setConfidenceScore(service.getConfidenceScore());
                    dto.setConfidence(scoreToConfidence(service.getConfidenceScore()));
                    dto.setIdentitySource(IdentitySource.MDNS_RULE);
                    dto.addEvidence("mDNS service = " + service.getServiceName());
                    return dto;
                })
                .orElse(null);
    }

    private DeviceIdentityResultDto matchRules(String vendor, String hostname, String mdns) {
        for (DeviceIdentityRuleDefinition rule : knowledgeBase.getRules()) {
            if (!ruleMatches(rule, vendor, hostname, mdns)) {
                continue;
            }
            DeviceIdentityResultDto dto = new DeviceIdentityResultDto();
            dto.setDisplayName(rule.getDisplayName());
            dto.setDeviceType(parseDeviceType(rule.getDeviceType()));
            dto.setConfidenceScore(rule.getConfidenceScore());
            dto.setConfidence(scoreToConfidence(rule.getConfidenceScore()));
            dto.setIdentitySource(resolveRuleSource(rule, vendor, hostname, mdns));
            dto.addEvidence("Matched rule: " + rule.getRuleName());
            if (vendor != null && !rule.getVendorContains().isEmpty()) {
                dto.addEvidence("Vendor contains match");
            }
            if (hostname != null && !rule.getHostnameContains().isEmpty()) {
                dto.addEvidence("Hostname contains match");
            }
            if (mdns != null && !rule.getMdnsContains().isEmpty()) {
                dto.addEvidence("mDNS contains match");
            }
            return dto;
        }
        return null;
    }

    private boolean ruleMatches(DeviceIdentityRuleDefinition rule, String vendor, String hostname, String mdns) {
        if (!rule.getVendorContains().isEmpty() && !containsAny(vendor, rule.getVendorContains())) {
            return false;
        }
        if (!rule.getHostnameContains().isEmpty() && !containsAny(hostname, rule.getHostnameContains())) {
            return false;
        }
        if (!rule.getMdnsContains().isEmpty() && mdns != null && !containsAny(mdns, rule.getMdnsContains())) {
            return false;
        }
        boolean hasCondition = !rule.getVendorContains().isEmpty()
                || !rule.getHostnameContains().isEmpty()
                || !rule.getMdnsContains().isEmpty();
        return hasCondition;
    }

    private IdentitySource resolveRuleSource(
            DeviceIdentityRuleDefinition rule, String vendor, String hostname, String mdns) {
        boolean hasVendor = !rule.getVendorContains().isEmpty();
        boolean hasHostname = !rule.getHostnameContains().isEmpty();
        boolean hasMdns = !rule.getMdnsContains().isEmpty();
        if (hasMdns && mdns != null && containsAny(mdns, rule.getMdnsContains())) {
            return IdentitySource.MDNS_RULE;
        }
        if (hasVendor && hasHostname) {
            return IdentitySource.COMBINED_RULE;
        }
        if (hasHostname) {
            return IdentitySource.HOSTNAME_RULE;
        }
        return IdentitySource.OUI_RULE;
    }

    private void mergeStronger(DeviceIdentityResultDto target, DeviceIdentityResultDto candidate) {
        if (candidate.getConfidenceScore() >= target.getConfidenceScore()) {
            target.setDisplayName(candidate.getDisplayName());
            target.setDeviceType(candidate.getDeviceType());
            target.setConfidence(candidate.getConfidence());
            target.setConfidenceScore(candidate.getConfidenceScore());
            target.setIdentitySource(candidate.getIdentitySource());
            target.getEvidence().clear();
            target.getEvidence().addAll(candidate.getEvidence());
        }
    }

    static boolean containsAny(String value, java.util.List<String> needles) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String lower = value.toLowerCase(Locale.ROOT);
        for (String needle : needles) {
            if (needle != null && lower.contains(needle.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    static String normalizeSignal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    static DeviceType parseDeviceType(String raw) {
        if (raw == null || raw.isBlank()) {
            return DeviceType.UNKNOWN;
        }
        try {
            return DeviceType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return DeviceType.UNKNOWN;
        }
    }

    static Confidence scoreToConfidence(int score) {
        if (score >= 100) {
            return Confidence.CONFIRMED;
        }
        if (score >= 70) {
            return Confidence.LIKELY;
        }
        if (score >= 40) {
            return Confidence.LOW;
        }
        return Confidence.UNKNOWN;
    }
}
