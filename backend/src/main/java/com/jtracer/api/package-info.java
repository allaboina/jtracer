/**
 * REST API layer (Phase 6).
 *
 * <p>Implemented endpoints:
 * <ul>
 *   <li>{@code GET /api/v1/system/health}</li>
 *   <li>{@code GET /api/v1/processes}</li>
 *   <li>{@code GET /api/v1/processes/{id}}</li>
 *   <li>{@code GET /api/v1/processes/{id}/metrics}</li>
 *   <li>{@code GET /api/v1/processes/{id}/connections}</li>
 *   <li>{@code GET /api/v1/connections}</li>
 *   <li>{@code GET /api/v1/domains}</li>
 *   <li>{@code GET /api/v1/devices}</li>
 *   <li>{@code GET /api/v1/devices/{id}}</li>
 *   <li>{@code POST /api/v1/devices/{id}/label}</li>
 * </ul>
 *
 * <p>Planned endpoints per {@code docs/API_CONTRACT.md}:
 * <ul>
 *   <li>{@code GET /api/v1/processes}</li>
 *   <li>{@code GET /api/v1/connections}</li>
 *   <li>{@code GET /api/v1/devices}</li>
 *   <li>{@code GET /api/v1/insights}</li>
 * </ul>
 */
package com.jtracer.api;
