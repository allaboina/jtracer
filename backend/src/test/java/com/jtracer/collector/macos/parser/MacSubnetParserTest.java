package com.jtracer.collector.macos.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jtracer.dto.collector.SubnetInfoDto;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class MacSubnetParserTest {

    @Test
    void parsesSubnetFromRouteAndIfconfigFixtures() throws IOException {
        String route = loadFixture("fixtures/macos/route-default-sample.txt");
        String ifconfig = loadFixture("fixtures/macos/ifconfig-en0-sample.txt");

        SubnetInfoDto subnet = MacSubnetParser.parse(route, ifconfig);

        assertEquals("en0", subnet.getAdapterName());
        assertEquals("192.168.1.1", subnet.getGateway());
        assertEquals("192.168.1.220", subnet.getLocalIp());
        assertEquals("0xffffff00", subnet.getNetmask());
        assertEquals("192.168.1.0/24", subnet.getSubnetCidr());
    }

    @Test
    void computesCidrFromNetmask() {
        assertEquals("192.168.1.0/24", MacSubnetParser.toCidr("192.168.1.220", "0xffffff00"));
        assertEquals(24, MacSubnetParser.netmaskHexToPrefix("0xffffff00"));
    }

    private String loadFixture(String path) throws IOException {
        return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
    }
}
