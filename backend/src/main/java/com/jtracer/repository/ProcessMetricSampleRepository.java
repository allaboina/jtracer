package com.jtracer.repository;

import com.jtracer.domain.entity.ProcessMetricSample;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessMetricSampleRepository extends JpaRepository<ProcessMetricSample, String> {
}
