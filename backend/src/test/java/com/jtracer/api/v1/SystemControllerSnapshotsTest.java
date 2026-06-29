package com.jtracer.api.v1;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jtracer.api.ApiExceptionHandler;
import com.jtracer.api.v1.dto.SystemHealthSnapshotPointDto;
import com.jtracer.service.SystemHealthQueryService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SystemController.class)
@Import(ApiExceptionHandler.class)
class SystemControllerSnapshotsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SystemHealthQueryService systemHealthQueryService;

    @Test
    void returnsHealthSnapshotHistory() throws Exception {
        SystemHealthSnapshotPointDto point = new SystemHealthSnapshotPointDto();
        point.setTimestamp(Instant.parse("2026-06-15T12:00:00Z"));
        point.setCpuPct(new BigDecimal("22.5"));
        point.setMemoryPct(new BigDecimal("68.0"));

        when(systemHealthQueryService.listSnapshots(60, null)).thenReturn(List.of(point));

        mockMvc.perform(get("/api/v1/system/snapshots").param("minutes", "60"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].cpuPct").value(22.5))
                .andExpect(jsonPath("$.data[0].memoryPct").value(68.0));
    }
}
