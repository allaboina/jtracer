package com.jtracer.collector.macos.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jtracer.domain.enums.Confidence;
import com.jtracer.dto.collector.LanDeviceRawDto;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class MacArpParserTest {

    @Test
    void parsesLanDevicesFromFixture() throws IOException {
        String output = loadFixture("fixtures/macos/arp-sample.txt");
        List<LanDeviceRawDto> devices = MacArpParser.parse(output);

        assertEquals(3, devices.size());

        LanDeviceRawDto router = findByIp(devices, "192.168.1.1");
        assertEquals("aa:bb:cc:dd:ee:01", router.getMacAddress());
        assertEquals("router.lan", router.getHostname());
        assertEquals("en0", router.getAdapterName());
        assertEquals(Confidence.LIKELY, router.getConfidence());

        LanDeviceRawDto iphone = findByIp(devices, "192.168.1.88");
        assertEquals("iphone.local", iphone.getHostname());
    }

    @Test
    void skipsBroadcastMulticastAndIncompleteEntries() throws IOException {
        List<LanDeviceRawDto> devices = MacArpParser.parse(loadFixture("fixtures/macos/arp-sample.txt"));
        assertTrue(devices.stream().noneMatch(d -> "192.168.1.255".equals(d.getIpAddress())));
        assertTrue(devices.stream().noneMatch(d -> "224.0.0.251".equals(d.getIpAddress())));
        assertTrue(devices.stream().noneMatch(d -> "192.168.1.100".equals(d.getIpAddress())));
    }

    @Test
    void classifiesPrivateIpsAsLanCandidates() {
        assertTrue(MacArpParser.isLanCandidate("192.168.1.10"));
        assertTrue(MacArpParser.isLanCandidate("10.0.0.5"));
        assertFalse(MacArpParser.isLanCandidate("93.184.216.34"));
        assertFalse(MacArpParser.isLanCandidate("192.168.1.255"));
    }

    @Test
    void normalizesUnknownHostnameToNull() {
        assertNull(MacArpParser.normalizeHostname("?"));
        assertEquals("router", MacArpParser.normalizeHostname("router"));
    }

    private LanDeviceRawDto findByIp(List<LanDeviceRawDto> devices, String ip) {
        return devices.stream()
                .filter(d -> ip.equals(d.getIpAddress()))
                .findFirst()
                .orElseThrow();
    }

    private String loadFixture(String path) throws IOException {
        return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
    }
}
