package com.jtracer.collector.macos.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jtracer.dto.collector.ProcessMetricSnapshotDto;
import com.jtracer.dto.collector.ProcessSnapshotDto;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class PsOutputParserTest {

    @Test
    void parsesProcessSnapshotsFromFixture() throws IOException {
        String output = loadFixture("fixtures/macos/ps-sample.txt");
        List<ProcessSnapshotDto> snapshots = PsOutputParser.parseSnapshots(output);

        assertEquals(5, snapshots.size());

        ProcessSnapshotDto chrome = snapshots.stream()
                .filter(p -> p.getPid() == 2245)
                .findFirst()
                .orElseThrow();
        assertEquals(501, chrome.getParentPid());
        assertEquals("Google Chrome", chrome.getProcessName());
        assertTrue(chrome.getExecutablePath().contains("Google Chrome.app"));
        assertTrue(chrome.getCommandLine().contains("Google Chrome"));
    }

    @Test
    void parsesProcessMetricsFromFixture() throws IOException {
        String output = loadFixture("fixtures/macos/ps-sample.txt");
        List<ProcessMetricSnapshotDto> metrics = PsOutputParser.parseMetrics(output);

        assertEquals(5, metrics.size());

        ProcessMetricSnapshotDto cursor = metrics.stream()
                .filter(m -> m.getPid() == 3301)
                .findFirst()
                .orElseThrow();
        assertEquals(0, new BigDecimal("8.5").compareTo(cursor.getCpuPct()));
        assertEquals(0, new BigDecimal("1.1").compareTo(cursor.getMemoryPct()));
        assertTrue(cursor.getRssMb().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void extractsProcessNameFromKernelProcess() {
        assertEquals("[kernel]", PsOutputParser.extractProcessName("[kernel]"));
        assertEquals("logd", PsOutputParser.extractProcessName("/usr/libexec/logd"));
    }

    private String loadFixture(String path) throws IOException {
        return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
    }
}
