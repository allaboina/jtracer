package com.jtracer.api.v1;

import com.jtracer.api.NoHealthDataException;
import com.jtracer.api.common.ApiResponse;
import com.jtracer.api.v1.dto.SystemHealthDataDto;
import com.jtracer.api.v1.dto.SystemHealthSnapshotPointDto;
import com.jtracer.service.SystemHealthQueryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
public class SystemController {

    private final SystemHealthQueryService systemHealthQueryService;

    public SystemController(SystemHealthQueryService systemHealthQueryService) {
        this.systemHealthQueryService = systemHealthQueryService;
    }

    @GetMapping("/health")
    public ApiResponse<SystemHealthDataDto> getHealth() {
        SystemHealthDataDto health = systemHealthQueryService
                .getCurrentHealth()
                .orElseThrow(NoHealthDataException::new);
        return ApiResponse.ok(health);
    }

    @GetMapping("/snapshots")
    public ApiResponse<List<SystemHealthSnapshotPointDto>> listSnapshots(
            @RequestParam(required = false) Integer minutes, @RequestParam(required = false) Integer hours) {
        return ApiResponse.ok(systemHealthQueryService.listSnapshots(minutes, hours));
    }
}
