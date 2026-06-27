package com.jtracer.service;

import com.jtracer.api.v1.dto.ProcessConnectionDto;
import com.jtracer.api.v1.dto.ProcessDetailDto;
import com.jtracer.api.v1.dto.ProcessMetricPointDto;
import com.jtracer.api.v1.dto.ProcessSummaryDto;
import java.util.List;
import java.util.Optional;

public interface ProcessQueryService {

    List<ProcessSummaryDto> listProcesses(String sort, Integer limit, String search);

    Optional<ProcessDetailDto> getProcess(String processId);

    List<ProcessMetricPointDto> getProcessMetrics(String processId, Integer minutes);

    List<ProcessConnectionDto> getProcessConnections(String processId);
}
