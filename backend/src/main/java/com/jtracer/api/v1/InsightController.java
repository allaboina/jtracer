package com.jtracer.api.v1;

import com.jtracer.api.common.ApiResponse;
import com.jtracer.api.v1.dto.InsightSummaryDto;
import com.jtracer.service.InsightQueryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/insights")
public class InsightController {

    private final InsightQueryService insightQueryService;

    public InsightController(InsightQueryService insightQueryService) {
        this.insightQueryService = insightQueryService;
    }

    @GetMapping
    public ApiResponse<List<InsightSummaryDto>> listInsights() {
        return ApiResponse.ok(insightQueryService.listActiveInsights());
    }

    @PostMapping("/{insightId}/dismiss")
    public ApiResponse<InsightSummaryDto> dismissInsight(@PathVariable String insightId) {
        return ApiResponse.ok(insightQueryService.dismissInsight(insightId));
    }
}
