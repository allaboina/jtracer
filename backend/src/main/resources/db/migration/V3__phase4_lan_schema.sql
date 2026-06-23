-- Phase 4 LAN schema: scan sessions and discovered devices

CREATE TABLE network_scan_sessions (
    id                      TEXT PRIMARY KEY,
    observation_session_id  TEXT NOT NULL,
    started_at              TEXT NOT NULL,
    ended_at                TEXT,
    subnet_cidr             TEXT,
    adapter_name            TEXT,
    scanned_host_count      INTEGER,
    online_device_count     INTEGER,
    unknown_device_count    INTEGER,
    status                  TEXT NOT NULL,
    failure_reason          TEXT,
    FOREIGN KEY (observation_session_id) REFERENCES observation_sessions (id)
);

CREATE INDEX idx_network_scan_sessions_observation ON network_scan_sessions (observation_session_id, started_at);

CREATE TABLE lan_devices (
    id                  TEXT PRIMARY KEY,
    scan_session_id     TEXT NOT NULL,
    ip_address          TEXT NOT NULL,
    mac_address         TEXT,
    hostname            TEXT,
    mdns_name           TEXT,
    vendor              TEXT,
    device_identity_id  TEXT,
    device_type         TEXT,
    status              TEXT NOT NULL,
    first_seen_at       TEXT NOT NULL,
    last_seen_at        TEXT NOT NULL,
    confidence          TEXT NOT NULL,
    source_adapter      TEXT,
    FOREIGN KEY (scan_session_id) REFERENCES network_scan_sessions (id)
);

CREATE INDEX idx_lan_devices_ip ON lan_devices (ip_address);
CREATE INDEX idx_lan_devices_mac ON lan_devices (mac_address);
CREATE INDEX idx_lan_devices_status ON lan_devices (status);
CREATE UNIQUE INDEX idx_lan_devices_mac_unique ON lan_devices (mac_address) WHERE mac_address IS NOT NULL;
