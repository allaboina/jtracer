package com.jtracer.service;

import com.jtracer.domain.entity.ObservationSession;

/**
 * Manages observation session lifecycle and capture mode selection.
 */
public interface ObservationSessionService {

    ObservationSession getCurrentSession();

    ObservationSession ensureCurrentSession();
}
