-- ─────────────────────────────────────────────────────────────────────────────
-- V7: Promo Codes
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE promo_codes
(
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(50)     NOT NULL UNIQUE,
    description     VARCHAR(200),
    discount_type   VARCHAR(20)     NOT NULL,
    discount_value  NUMERIC(10, 2)  NOT NULL,
    min_order_amount NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    usage_limit     INTEGER,
    used_count      INTEGER         NOT NULL DEFAULT 0,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    expires_at      TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),

    CONSTRAINT chk_promo_discount_type  CHECK (discount_type IN ('PERCENTAGE', 'FLAT')),
    CONSTRAINT chk_promo_discount_value CHECK (discount_value > 0),
    CONSTRAINT chk_promo_used_count     CHECK (used_count >= 0)
);

CREATE INDEX idx_promo_codes_code      ON promo_codes (code);
CREATE INDEX idx_promo_codes_is_active ON promo_codes (is_active);