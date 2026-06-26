package com.jtracer.persistence;

/**
 * Abstraction for swappable storage backends.
 *
 * <p>Implementations (future):
 * <ul>
 *   <li>{@code LocalSQLitePersistenceProvider} — MVP/default</li>
 *   <li>{@code TursoPersistenceProvider} — future cloud/sync option</li>
 *   <li>{@code PostgresPersistenceProvider} — future team/server mode</li>
 * </ul>
 *
 * <p>MVP uses Spring Data JPA + Flyway + SQLite directly; this interface documents the
 * planned migration path. See {@code docs/SYSTEM_DESIGN.md}.
 */
public interface PersistenceProvider {

    /** Provider id matching {@code jtracer.persistence.provider} config. */
    String providerId();

    /** Whether this provider can serve requests in the current environment. */
    boolean isAvailable();
}
