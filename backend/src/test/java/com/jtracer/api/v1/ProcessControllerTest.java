package com.jtracer.api.v1;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jtracer.api.ApiExceptionHandler;
import com.jtracer.api.v1.dto.ProcessDetailDto;
import com.jtracer.api.v1.dto.ProcessSummaryDto;
import com.jtracer.domain.enums.ProcessStatus;
import com.jtracer.service.ProcessQueryService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProcessController.class)
@Import(ApiExceptionHandler.class)
class ProcessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcessQueryService processQueryService;

    @Test
    void listsProcesses() throws Exception {
        ProcessSummaryDto summary = new ProcessSummaryDto();
        summary.setProcessId("proc-1");
        summary.setPid(100);
        summary.setProcessName("curl");
        summary.setStatus(ProcessStatus.RUNNING);
        summary.setConnectionCount(2);

        when(processQueryService.listProcesses(null, null, null)).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/processes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].processId").value("proc-1"))
                .andExpect(jsonPath("$.data[0].processName").value("curl"));
    }

    @Test
    void returnsNotFoundForMissingProcess() throws Exception {
        when(processQueryService.getProcess("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/processes/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }

    @Test
    void returnsProcessDetail() throws Exception {
        ProcessDetailDto detail = new ProcessDetailDto();
        detail.setProcessId("proc-1");
        detail.setPid(100);
        detail.setProcessName("curl");
        detail.setStatus(ProcessStatus.RUNNING);

        when(processQueryService.getProcess("proc-1")).thenReturn(Optional.of(detail));

        mockMvc.perform(get("/api/v1/processes/proc-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.processName").value("curl"));
    }

    @Test
    void passesSearchAndSortParameters() throws Exception {
        when(processQueryService.listProcesses(eq("memory"), eq(10), eq("chrome")))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/processes").param("sort", "memory").param("limit", "10").param("search", "chrome"))
                .andExpect(status().isOk());
    }
}
