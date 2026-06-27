package com.jtracer.api.v1;

import com.jtracer.api.common.ApiResponse;
import com.jtracer.api.v1.dto.ConnectionSummaryDto;
import com.jtracer.service.ConnectionQueryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/connections")
public class ConnectionController {

    private final ConnectionQueryService connectionQueryService;

    public ConnectionController(ConnectionQueryService connectionQueryService) {
        this.connectionQueryService = connectionQueryService;
    }

    @GetMapping
    public ApiResponse<List<ConnectionSummaryDto>> listConnections(
            @RequestParam(required = false) String protocol,
            @RequestParam(required = false) String processId,
            @RequestParam(required = false) String sort) {
        return ApiResponse.ok(connectionQueryService.listConnections(protocol, processId, sort));
    }
}
