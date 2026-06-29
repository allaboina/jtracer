-- Phase 6/9: insights table for evidence-based recommendations

CREATE TABLE insights (
    id              TEXT PRIMARY KEY,
    session_id      TEXT NOT NULL,
    entity_type     TEXT NOT NULL,
    entity_id       TEXT NOT NULL,
    severity        TEXT NOT NULL,
    title           TEXT NOT NULL,
    explanation     TEXT NOT NULL,
    evidence_json   TEXT,
    generated_at    TEXT NOT NULL,
    rule_id         TEXT,
    status          TEXT NOT NULL,
    FOREIGN KEY (session_id) REFERENCES observation_sessions (id)
);

CREATE INDEX idx_insights_session_status ON insights (session_id, status, generated_at);
