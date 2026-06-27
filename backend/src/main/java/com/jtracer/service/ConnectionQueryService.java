package com.jtracer.service;

import com.jtracer.api.v1.dto.ConnectionSummaryDto;
import java.util.List;

public interface ConnectionQueryService {

    List<ConnectionSummaryDto> listConnections(String protocol, String processId, String sort);
}
