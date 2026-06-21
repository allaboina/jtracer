package com.jtracer.collector.macos.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class PmsetOutputParserTest {

    @Test
    void parsesBatteryStatusFromFixture() throws IOException {
        String output = new ClassPathResource("fixtures/macos/pmset-sample.txt")
                .getContentAsString(StandardCharsets.UTF_8);
        PmsetOutputParser.BatteryStatus status = PmsetOutputParser.parseBatteryStatus(output);

        assertNotNull(status);
        assertEquals(0, new BigDecimal("82").compareTo(status.percentage()));
        assertTrue(status.charging());
    }
}
