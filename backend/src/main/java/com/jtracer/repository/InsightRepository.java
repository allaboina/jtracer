package com.jtracer.repository;

import com.jtracer.domain.entity.Insight;
import com.jtracer.domain.enums.InsightStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsightRepository extends JpaRepository<Insight, String> {

    List<Insight> findBySession_IdAndStatusOrderByGeneratedAtDesc(String sessionId, InsightStatus status);
}
