-- ─────────────────────────────────────────────────────────────────────────────
-- V9: AI Sessions
-- messages stored as JSONB — array of {role, content, timestamp}
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE ai_sessions
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT          NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    session_token VARCHAR(100)    NOT NULL UNIQUE,
    messages      JSONB           NOT NULL DEFAULT '[]',
    last_active   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP WITH TIME ZONE,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100)
);

CREATE INDEX idx_ai_sessions_user_id       ON ai_sessions (user_id);
CREATE INDEX idx_ai_sessions_session_token ON ai_sessions (session_token);
CREATE INDEX idx_ai_sessions_last_active   ON ai_sessions (last_active DESC);