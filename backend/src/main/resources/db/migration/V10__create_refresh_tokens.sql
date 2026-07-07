-- ─────────────────────────────────────────────────────────────────────────────
-- V10: Refresh Tokens
-- One active refresh token per user.
-- Token is hashed before storing (SHA-256) — raw token only lives in the cookie.
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE refresh_tokens
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token_hash  VARCHAR(64) NOT NULL UNIQUE,
    expires_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100)
);

CREATE INDEX idx_refresh_tokens_user_id   ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens (token_hash);