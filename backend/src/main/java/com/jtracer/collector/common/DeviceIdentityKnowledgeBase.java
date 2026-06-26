package com.jtracer.collector.common;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * In-memory device identity knowledge loaded from bundled JSON files.
 */
public final class DeviceIdentityKnowledgeBase {

    private final Map<String, String> ouiVendors;
    private final List<DeviceIdentityRuleDefinition> rules;
    private final List<MdnsServiceDefinition> mdnsServices;

    public DeviceIdentityKnowledgeBase(
            Map<String, String> ouiVendors,
            List<DeviceIdentityRuleDefinition> rules,
            List<MdnsServiceDefinition> mdnsServices) {
        this.ouiVendors = ouiVendors != null ? Map.copyOf(ouiVendors) : Map.of();
        this.rules = rules != null ? List.copyOf(rules) : List.of();
        this.mdnsServices = mdnsServices != null ? List.copyOf(mdnsServices) : List.of();
    }

    public Map<String, String> getOuiVendors() {
        return ouiVendors;
    }

    public List<DeviceIdentityRuleDefinition> getRules() {
        return rules;
    }

    public List<MdnsServiceDefinition> getMdnsServices() {
        return mdnsServices;
    }

    public static DeviceIdentityKnowledgeBase empty() {
        return new DeviceIdentityKnowledgeBase(Collections.emptyMap(), Collections.emptyList(), Collections.emptyList());
    }
}
