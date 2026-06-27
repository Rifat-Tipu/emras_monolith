-- ─────────────────────────────────────────────────────────────────────────────
-- V6: Payments
-- Supports COD, manual bKash, manual Nagad (Phase 1-4)
-- payment_method will expand to BKASH_GATEWAY, NAGAD_GATEWAY in Phase 5
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE payments
(
    id                BIGSERIAL PRIMARY KEY,
    order_id          BIGINT          NOT NULL REFERENCES orders (id) ON DELETE RESTRICT,
    payment_method    VARCHAR(30)     NOT NULL,
    status            VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    amount            NUMERIC(10, 2)  NOT NULL,
    transaction_id    VARCHAR(100),
    gateway_ref       VARCHAR(200),
    verified_by       VARCHAR(150),
    verified_at       TIMESTAMP WITH TIME ZONE,
    rejection_reason  TEXT,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP WITH TIME ZONE,
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100),

    CONSTRAINT chk_payments_method CHECK (
        payment_method IN ('CASH_ON_DELIVERY', 'BKASH_MANUAL', 'NAGAD_MANUAL', 'BKASH_GATEWAY', 'NAGAD_GATEWAY')
        ),
    CONSTRAINT chk_payments_status CHECK (
        status IN ('PENDING', 'SUBMITTED', 'VERIFIED', 'FAILED', 'REFUNDED')
        ),
    CONSTRAINT chk_payments_amount CHECK (amount > 0)
);

CREATE INDEX idx_payments_order_id ON payments (order_id);
CREATE INDEX idx_payments_status   ON payments (status);