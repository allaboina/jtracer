package com.jtracer.repository;

import com.jtracer.domain.entity.ProcessMetricSample;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessMetricSampleRepository extends JpaRepository<ProcessMetricSample, String> {

    java.util.Optional<ProcessMetricSample> findFirstByObservedProcess_IdOrderBySampledAtDesc(String observedProcessId);

    java.util.List<ProcessMetricSample> findByObservedProcess_IdAndSampledAtAfterOrderBySampledAtAsc(
            String observedProcessId, java.time.Instant sampledAtAfter);

    java.util.List<ProcessMetricSample> findBySession_IdOrderBySampledAtDesc(String sessionId);
}
