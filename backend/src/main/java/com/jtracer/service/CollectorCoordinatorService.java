package com.jtracer.service;

/**
 * Schedules and coordinates host collector polling intervals.
 */
public interface CollectorCoordinatorService {

    void startCollectors();

    void stopCollectors();
}
