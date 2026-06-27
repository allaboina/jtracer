package com.jtracer.api.v1;

import com.jtracer.api.common.ApiResponse;
import com.jtracer.api.v1.dto.DomainSummaryDto;
import com.jtracer.service.DomainQueryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/domains")
public class DomainController {

    private final DomainQueryService domainQueryService;

    public DomainController(DomainQueryService domainQueryService) {
        this.domainQueryService = domainQueryService;
    }

    @GetMapping
    public ApiResponse<List<DomainSummaryDto>> listDomains(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String processId) {
        return ApiResponse.ok(domainQueryService.listDomains(sort, processId));
    }
}
