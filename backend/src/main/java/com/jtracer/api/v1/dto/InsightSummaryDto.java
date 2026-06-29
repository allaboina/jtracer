package com.jtracer.api.v1.dto;

import com.jtracer.domain.enums.InsightEntityType;
import com.jtracer.domain.enums.Severity;
import java.time.Instant;

public class InsightSummaryDto {

    private String insightId;
    private InsightEntityType entityType;
    private String entityId;
    private Severity severity;
    private String title;
    private String explanation;
    private Instant generatedAt;

    public String getInsightId() {
        return insightId;
    }

    public void setInsightId(String insightId) {
        this.insightId = insightId;
    }

    public InsightEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(InsightEntityType entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }
}
