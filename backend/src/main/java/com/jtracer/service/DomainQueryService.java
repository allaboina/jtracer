package com.jtracer.service;

import com.jtracer.api.v1.dto.DomainSummaryDto;
import java.util.List;

public interface DomainQueryService {

    List<DomainSummaryDto> listDomains(String sort, String processId);
}
