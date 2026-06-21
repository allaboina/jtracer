package com.jtracer.service;

/**
 * Applies configurable data retention policies to historical samples.
 */
public interface DataRetentionService {

    void purgeExpiredData();
}
