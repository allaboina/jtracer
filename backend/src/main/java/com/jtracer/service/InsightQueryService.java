package com.jtracer.service;

import com.jtracer.api.v1.dto.InsightSummaryDto;
import java.util.List;

/**
 * Read and update insight records for API responses.
 */
public interface InsightQueryService {

    List<InsightSummaryDto> listActiveInsights();

    InsightSummaryDto dismissInsight(String insightId);
}
