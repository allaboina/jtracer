package com.jtracer.api.v1;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jtracer.api.ApiExceptionHandler;
import com.jtracer.api.v1.dto.SystemHealthDataDto;
import com.jtracer.service.SystemHealthQueryService;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SystemController.class)
@Import(ApiExceptionHandler.class)
class SystemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SystemHealthQueryService systemHealthQueryService;

    @Test
    void returnsCurrentSystemHealth() throws Exception {
        SystemHealthDataDto dto = new SystemHealthDataDto();
        dto.setCpuPct(new BigDecimal("28.5"));
        dto.setMemoryPct(new BigDecimal("71.2"));
        dto.setUsedMemoryMb(new BigDecimal("11234"));
        dto.setTotalMemoryMb(new BigDecimal("16384"));
        dto.setDiskUsagePct(new BigDecimal("61.4"));
        dto.setBatteryPct(new BigDecimal("82"));
        dto.setBatteryCharging(true);
        dto.setActiveProcessCount(221);
        dto.setActiveConnectionCount(43);
        dto.setOnlineLanDeviceCount(9);

        when(systemHealthQueryService.getCurrentHealth()).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/v1/system/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cpuPct").value(28.5))
                .andExpect(jsonPath("$.data.memoryPct").value(71.2))
                .andExpect(jsonPath("$.data.activeProcessCount").value(221))
                .andExpect(jsonPath("$.data.onlineLanDeviceCount").value(9))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void returnsServiceUnavailableWhenNoHealthSnapshot() throws Exception {
        when(systemHealthQueryService.getCurrentHealth()).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/system/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("NO_HEALTH_DATA"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
