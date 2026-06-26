-- Phase 5 device identity schema

CREATE TABLE device_identities (
    id              TEXT PRIMARY KEY,
    display_name    TEXT NOT NULL,
    device_type     TEXT NOT NULL,
    manufacturer    TEXT,
    model           TEXT,
    confidence      TEXT NOT NULL,
    identity_source TEXT NOT NULL,
    evidence        TEXT,
    created_at      TEXT NOT NULL,
    updated_at      TEXT NOT NULL
);

CREATE TABLE device_identity_rules (
    id                  TEXT PRIMARY KEY,
    rule_name           TEXT NOT NULL,
    vendor_contains     TEXT,
    hostname_contains   TEXT,
    mac_oui_prefix      TEXT,
    mdns_contains       TEXT,
    device_type         TEXT NOT NULL,
    display_name        TEXT NOT NULL,
    confidence          TEXT NOT NULL,
    priority            INTEGER NOT NULL,
    enabled             INTEGER NOT NULL DEFAULT 1,
    created_at          TEXT NOT NULL,
    updated_at          TEXT NOT NULL
);

CREATE TABLE user_device_labels (
    id              TEXT PRIMARY KEY,
    lan_device_id   TEXT NOT NULL,
    mac_address     TEXT,
    label           TEXT NOT NULL,
    device_type     TEXT,
    created_at      TEXT NOT NULL,
    updated_at      TEXT NOT NULL,
    FOREIGN KEY (lan_device_id) REFERENCES lan_devices (id)
);

CREATE INDEX idx_device_identity_rules_enabled_priority ON device_identity_rules (enabled, priority DESC);
CREATE INDEX idx_user_device_labels_mac ON user_device_labels (mac_address);
CREATE INDEX idx_lan_devices_device_identity ON lan_devices (device_identity_id);
