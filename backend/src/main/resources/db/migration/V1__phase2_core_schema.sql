-- Phase 2 core schema: host, session, processes, metrics, system health

CREATE TABLE host_machines (
    id              TEXT PRIMARY KEY,
    hostname        TEXT NOT NULL,
    os_type         TEXT NOT NULL,
    os_version      TEXT,
    architecture    TEXT,
    primary_user    TEXT,
    created_at      TEXT NOT NULL,
    last_seen_at    TEXT NOT NULL
);

CREATE TABLE observation_sessions (
    id                TEXT PRIMARY KEY,
    host_machine_id   TEXT NOT NULL,
    started_at        TEXT NOT NULL,
    ended_at          TEXT,
    capture_mode      TEXT NOT NULL,
    platform_adapter  TEXT,
    app_version       TEXT,
    status            TEXT NOT NULL,
    failure_reason    TEXT,
    FOREIGN KEY (host_machine_id) REFERENCES host_machines (id)
);

CREATE TABLE observed_processes (
    id               TEXT PRIMARY KEY,
    session_id       TEXT NOT NULL,
    pid              INTEGER NOT NULL,
    parent_pid       INTEGER,
    process_name     TEXT NOT NULL,
    executable_path  TEXT,
    command_line     TEXT,
    app_name         TEXT,
    app_bundle_id    TEXT,
    user_name        TEXT,
    first_seen_at    TEXT NOT NULL,
    last_seen_at     TEXT NOT NULL,
    status           TEXT NOT NULL,
    FOREIGN KEY (session_id) REFERENCES observation_sessions (id)
);

CREATE INDEX idx_observed_processes_session_pid ON observed_processes (session_id, pid);
CREATE INDEX idx_observed_processes_session_status ON observed_processes (session_id, status);

CREATE TABLE process_metric_samples (
    id                  TEXT PRIMARY KEY,
    observed_process_id TEXT NOT NULL,
    session_id          TEXT NOT NULL,
    sampled_at          TEXT NOT NULL,
    cpu_pct             REAL,
    memory_pct          REAL,
    rss_mb              REAL,
    thread_count        INTEGER,
    open_file_count     INTEGER,
    FOREIGN KEY (observed_process_id) REFERENCES observed_processes (id),
    FOREIGN KEY (session_id) REFERENCES observation_sessions (id)
);

CREATE INDEX idx_process_metric_samples_process ON process_metric_samples (observed_process_id, sampled_at);

CREATE TABLE system_health_snapshots (
    id                       TEXT PRIMARY KEY,
    session_id               TEXT NOT NULL,
    sampled_at               TEXT NOT NULL,
    total_cpu_pct            REAL,
    total_memory_pct         REAL,
    used_memory_mb           REAL,
    total_memory_mb          REAL,
    disk_usage_pct           REAL,
    battery_pct              REAL,
    battery_charging           INTEGER,
    active_process_count     INTEGER,
    active_connection_count  INTEGER,
    online_lan_device_count  INTEGER,
    FOREIGN KEY (session_id) REFERENCES observation_sessions (id)
);

CREATE INDEX idx_system_health_snapshots_session ON system_health_snapshots (session_id, sampled_at);
