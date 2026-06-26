package com.jtracer.collector.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtracer.config.JtracerProperties;
import com.jtracer.domain.enums.Confidence;
import com.jtracer.domain.enums.DeviceType;
import com.jtracer.domain.enums.IdentitySource;
import com.jtracer.dto.collector.DeviceIdentityResultDto;
import com.jtracer.dto.collector.LanDeviceRawDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RuleBasedDeviceIdentityResolverTest {

    private RuleBasedDeviceIdentityResolver resolver;

    @BeforeEach
    void setUp() {
        JtracerProperties properties = new JtracerProperties();
        properties.getKnowledgeBase().setBasePath("classpath:knowledge-base");
        DeviceIdentityKnowledgeBase knowledgeBase = new KnowledgeBaseLoader(new ObjectMapper(), properties).load();
        resolver = new RuleBasedDeviceIdentityResolver(knowledgeBase);
    }

    @Test
    void resolvesAmazonEchoFromVendorAndHostname() {
        LanDeviceRawDto raw = new LanDeviceRawDto();
        raw.setMacAddress("fc:a6:67:11:22:33");
        raw.setHostname("echo-bedroom.local");

        DeviceIdentityResultDto result = resolver.resolveIdentity(raw);

        assertEquals("Amazon Echo", result.getDisplayName());
        assertEquals(DeviceType.SMART_SPEAKER, result.getDeviceType());
        assertEquals(Confidence.LIKELY, result.getConfidence());
        assertEquals(IdentitySource.COMBINED_RULE, result.getIdentitySource());
        assertTrue(result.getEvidence().stream().anyMatch(e -> e.contains("Vendor")));
    }

    @Test
    void resolvesAppleIphoneFromHostname() {
        LanDeviceRawDto raw = new LanDeviceRawDto();
        raw.setMacAddress("a4:b1:c1:11:22:33");
        raw.setHostname("johns-iphone.local");

        DeviceIdentityResultDto result = resolver.resolveIdentity(raw);

        assertEquals("Apple iPhone", result.getDisplayName());
        assertEquals(DeviceType.PHONE, result.getDeviceType());
    }

    @Test
    void userLabelOverridesAutomaticClassification() {
        LanDeviceRawDto raw = new LanDeviceRawDto();
        raw.setMacAddress("a4:b1:c1:11:22:33");
        raw.setHostname("johns-iphone.local");

        DeviceIdentityResultDto result = resolver.resolveIdentity(raw, "Living Room iPad");

        assertEquals("Living Room iPad", result.getDisplayName());
        assertEquals(Confidence.CONFIRMED, result.getConfidence());
        assertEquals(IdentitySource.USER_LABEL, result.getIdentitySource());
    }

    @Test
    void unknownDeviceWhenNoSignalsMatch() {
        LanDeviceRawDto raw = new LanDeviceRawDto();
        raw.setIpAddress("192.168.1.99");

        DeviceIdentityResultDto result = resolver.resolveIdentity(raw);

        assertEquals("Unknown Device", result.getDisplayName());
        assertEquals(DeviceType.UNKNOWN, result.getDeviceType());
        assertEquals(Confidence.UNKNOWN, result.getConfidence());
    }

    @Test
    void vendorOnlyFallsBackToManufacturerDevice() {
        LanDeviceRawDto raw = new LanDeviceRawDto();
        raw.setMacAddress("b8:3e:59:11:22:33");

        DeviceIdentityResultDto result = resolver.resolveIdentity(raw);

        assertEquals("Roku Inc. Device", result.getDisplayName());
        assertEquals(DeviceType.UNKNOWN, result.getDeviceType());
        assertEquals(Confidence.LOW, result.getConfidence());
    }
}
