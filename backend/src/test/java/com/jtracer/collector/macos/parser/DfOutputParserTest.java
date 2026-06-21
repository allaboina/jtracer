package com.jtracer.collector.macos.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class DfOutputParserTest {

    @Test
    void parsesDiskUsagePercentFromFixture() throws IOException {
        String output = new ClassPathResource("fixtures/macos/df-sample.txt")
                .getContentAsString(StandardCharsets.UTF_8);
        BigDecimal usage = DfOutputParser.parseDiskUsagePercent(output);
        assertNotNull(usage);
        assertEquals(0, new BigDecimal("3").compareTo(usage));
    }
}
