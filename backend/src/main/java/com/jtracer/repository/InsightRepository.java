package com.jtracer.repository;

import com.jtracer.domain.entity.Insight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsightRepository extends JpaRepository<Insight, String> {
}
