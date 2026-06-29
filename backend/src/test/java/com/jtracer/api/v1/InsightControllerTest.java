package com.jtracer.api.v1;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jtracer.api.ApiExceptionHandler;
import com.jtracer.api.ResourceNotFoundException;
import com.jtracer.api.v1.dto.InsightSummaryDto;
import com.jtracer.domain.enums.Severity;
import com.jtracer.service.InsightQueryService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = InsightController.class)
@Import(ApiExceptionHandler.class)
class InsightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InsightQueryService insightQueryService;

    @Test
    void returnsActiveInsights() throws Exception {
        InsightSummaryDto insight = new InsightSummaryDto();
        insight.setInsightId("ins-1");
        insight.setSeverity(Severity.WARNING);
        insight.setTitle("High memory");
        insight.setExplanation("Chrome above 2 GB");
        insight.setGeneratedAt(Instant.parse("2026-06-15T12:00:00Z"));

        when(insightQueryService.listActiveInsights()).thenReturn(List.of(insight));

        mockMvc.perform(get("/api/v1/insights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].insightId").value("ins-1"))
                .andExpect(jsonPath("$.data[0].title").value("High memory"));
    }

    @Test
    void dismissesInsight() throws Exception {
        InsightSummaryDto dismissed = new InsightSummaryDto();
        dismissed.setInsightId("ins-1");
        dismissed.setTitle("High memory");

        when(insightQueryService.dismissInsight("ins-1")).thenReturn(dismissed);

        mockMvc.perform(post("/api/v1/insights/ins-1/dismiss"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.insightId").value("ins-1"));
    }

    @Test
    void returnsNotFoundForMissingInsight() throws Exception {
        when(insightQueryService.dismissInsight("missing"))
                .thenThrow(new ResourceNotFoundException("Insight", "missing"));

        mockMvc.perform(post("/api/v1/insights/missing/dismiss"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }
}
