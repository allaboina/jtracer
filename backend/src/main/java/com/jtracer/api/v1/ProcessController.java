package com.jtracer.api.v1;

import com.jtracer.api.ResourceNotFoundException;
import com.jtracer.api.common.ApiResponse;
import com.jtracer.api.v1.dto.ProcessConnectionDto;
import com.jtracer.api.v1.dto.ProcessDetailDto;
import com.jtracer.api.v1.dto.ProcessMetricPointDto;
import com.jtracer.api.v1.dto.ProcessSummaryDto;
import com.jtracer.service.ProcessQueryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/processes")
public class ProcessController {

    private final ProcessQueryService processQueryService;

    public ProcessController(ProcessQueryService processQueryService) {
        this.processQueryService = processQueryService;
    }

    @GetMapping
    public ApiResponse<List<ProcessSummaryDto>> listProcesses(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String search) {
        return ApiResponse.ok(processQueryService.listProcesses(sort, limit, search));
    }

    @GetMapping("/{processId}")
    public ApiResponse<ProcessDetailDto> getProcess(@PathVariable String processId) {
        ProcessDetailDto process = processQueryService
                .getProcess(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Process", processId));
        return ApiResponse.ok(process);
    }

    @GetMapping("/{processId}/metrics")
    public ApiResponse<List<ProcessMetricPointDto>> getProcessMetrics(
            @PathVariable String processId, @RequestParam(required = false) Integer minutes) {
        return ApiResponse.ok(processQueryService.getProcessMetrics(processId, minutes));
    }

    @GetMapping("/{processId}/connections")
    public ApiResponse<List<ProcessConnectionDto>> getProcessConnections(@PathVariable String processId) {
        return ApiResponse.ok(processQueryService.getProcessConnections(processId));
    }
}
