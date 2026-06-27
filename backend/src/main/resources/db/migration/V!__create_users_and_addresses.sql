-- ─────────────────────────────────────────────────────────────────────────────
-- V1: Users and Addresses
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    full_name     VARCHAR(100)        NOT NULL,
    email         VARCHAR(150)        NOT NULL UNIQUE,
    password_hash VARCHAR(255)        NOT NULL,
    role          VARCHAR(20)         NOT NULL DEFAULT 'CUSTOMER',
    is_active     BOOLEAN             NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP WITH TIME ZONE,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100),

    CONSTRAINT chk_users_role CHECK (role IN ('CUSTOMER', 'ADMIN', 'SUPER_ADMIN'))
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_role  ON users (role);

-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE addresses
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT              NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    label         VARCHAR(50)         NOT NULL DEFAULT 'Home',
    recipient_name VARCHAR(100)       NOT NULL,
    phone         VARCHAR(20)         NOT NULL,
    street        VARCHAR(255)        NOT NULL,
    area          VARCHAR(100),
    city          VARCHAR(100)        NOT NULL,
    district      VARCHAR(100)        NOT NULL,
    postal_code   VARCHAR(20),
    is_default    BOOLEAN             NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP WITH TIME ZONE,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100)
);

CREATE INDEX idx_addresses_user_id ON addresses (user_id);