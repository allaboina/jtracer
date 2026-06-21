package com.jtracer.collector.macos.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jtracer.domain.enums.ConnectionDirection;
import com.jtracer.domain.enums.ConnectionState;
import com.jtracer.domain.enums.ProtocolType;
import com.jtracer.dto.collector.ConnectionSnapshotDto;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class LsofOutputParserTest {

    @Test
    void parsesEstablishedOutboundConnectionsFromFixture() throws IOException {
        String output = loadFixture("fixtures/macos/lsof-sample.txt");
        List<ConnectionSnapshotDto> connections = LsofOutputParser.parse(output);

        assertEquals(4, connections.size());

        ConnectionSnapshotDto google = findByPid(connections, 919);
        assertEquals("Google", google.getProcessName());
        assertEquals(ProtocolType.TCP, google.getProtocol());
        assertEquals("10.0.0.220", google.getLocalIp());
        assertEquals(64884, google.getLocalPort());
        assertEquals("34.216.181.117", google.getRemoteIp());
        assertEquals(443, google.getRemotePort());
        assertEquals(ConnectionState.ACTIVE, google.getState());
        assertEquals(ConnectionDirection.OUTBOUND, google.getDirection());
        assertEquals("macos-lsof", google.getSourceAdapter());
    }

    @Test
    void classifiesLoopbackConnectionsAsLocal() throws IOException {
        String output = loadFixture("fixtures/macos/lsof-sample.txt");
        ConnectionSnapshotDto cursor = findByPid(LsofOutputParser.parse(output), 3301);

        assertEquals(ConnectionDirection.LOCAL, cursor.getDirection());
        assertEquals("127.0.0.1", cursor.getLocalIp());
        assertEquals("127.0.0.1", cursor.getRemoteIp());
    }

    @Test
    void skipsListenSocketsWithoutRemoteEndpoint() throws IOException {
        String output = loadFixture("fixtures/macos/lsof-sample.txt");
        List<ConnectionSnapshotDto> connections = LsofOutputParser.parse(output);

        assertTrue(connections.stream().noneMatch(c -> c.getPid() == 5000));
    }

    @Test
    void mapsTcpStates() {
        assertEquals(ConnectionState.ACTIVE, LsofOutputParser.mapState("ESTABLISHED"));
        assertEquals(ConnectionState.LISTENING, LsofOutputParser.mapState("LISTEN"));
        assertEquals(ConnectionState.CLOSED, LsofOutputParser.mapState("TIME_WAIT"));
        assertEquals(ConnectionState.UNKNOWN, LsofOutputParser.mapState(null));
    }

    @Test
    void normalizesIpv6Addresses() {
        assertEquals("fe80::1%en0", LsofOutputParser.normalizeAddress("[fe80::1%en0]"));
        assertEquals("34.216.181.117", LsofOutputParser.normalizeAddress("34.216.181.117"));
    }

    private ConnectionSnapshotDto findByPid(List<ConnectionSnapshotDto> connections, int pid) {
        return connections.stream()
                .filter(c -> c.getPid() == pid)
                .findFirst()
                .orElseThrow();
    }

    private String loadFixture(String path) throws IOException {
        return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
    }
}
