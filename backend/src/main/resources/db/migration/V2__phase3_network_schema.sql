-- Phase 3 network schema: endpoints, domains, connections

CREATE TABLE remote_endpoints (
    id              TEXT PRIMARY KEY,
    ip_address      TEXT NOT NULL,
    port            INTEGER NOT NULL,
    endpoint_type   TEXT NOT NULL,
    first_seen_at   TEXT NOT NULL,
    last_seen_at    TEXT NOT NULL
);

CREATE UNIQUE INDEX idx_remote_endpoints_ip_port ON remote_endpoints (ip_address, port);

CREATE TABLE domain_identities (
    id              TEXT PRIMARY KEY,
    hostname        TEXT NOT NULL,
    root_domain     TEXT,
    source          TEXT NOT NULL,
    confidence      TEXT NOT NULL,
    first_seen_at   TEXT NOT NULL,
    last_seen_at    TEXT NOT NULL
);

CREATE UNIQUE INDEX idx_domain_identities_hostname ON domain_identities (hostname);

CREATE TABLE network_connections (
    id                  TEXT PRIMARY KEY,
    session_id          TEXT NOT NULL,
    observed_process_id TEXT,
    protocol            TEXT NOT NULL,
    local_ip            TEXT,
    local_port          INTEGER,
    remote_ip           TEXT,
    remote_port         INTEGER,
    remote_endpoint_id  TEXT,
    domain_identity_id  TEXT,
    first_seen_at       TEXT NOT NULL,
    last_seen_at        TEXT NOT NULL,
    state               TEXT NOT NULL,
    direction           TEXT NOT NULL,
    confidence          TEXT NOT NULL,
    source_adapter      TEXT,
    FOREIGN KEY (session_id) REFERENCES observation_sessions (id),
    FOREIGN KEY (observed_process_id) REFERENCES observed_processes (id),
    FOREIGN KEY (remote_endpoint_id) REFERENCES remote_endpoints (id),
    FOREIGN KEY (domain_identity_id) REFERENCES domain_identities (id)
);

CREATE INDEX idx_network_connections_session ON network_connections (session_id);
CREATE INDEX idx_network_connections_session_state ON network_connections (session_id, state);
CREATE UNIQUE INDEX idx_network_connections_natural_key ON network_connections (
    session_id,
    protocol,
    local_ip,
    local_port,
    remote_ip,
    remote_port
);
