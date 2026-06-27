-- ─────────────────────────────────────────────────────────────────────────────
-- V5: Orders and Order Items
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE orders
(
    id               BIGSERIAL PRIMARY KEY,
    order_number     VARCHAR(30)         NOT NULL UNIQUE,
    user_id          BIGINT              NOT NULL REFERENCES users (id) ON DELETE RESTRICT,
    address_id       BIGINT              REFERENCES addresses (id) ON DELETE SET NULL,
    status           VARCHAR(30)         NOT NULL DEFAULT 'PLACED',
    payment_status   VARCHAR(30)         NOT NULL DEFAULT 'PENDING',
    subtotal         NUMERIC(10, 2)      NOT NULL,
    discount_amount  NUMERIC(10, 2)      NOT NULL DEFAULT 0.00,
    total_amount     NUMERIC(10, 2)      NOT NULL,
    promo_code       VARCHAR(50),
    notes            TEXT,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP WITH TIME ZONE,
    created_by       VARCHAR(100),
    updated_by       VARCHAR(100),

    CONSTRAINT chk_orders_status CHECK (
        status IN ('PLACED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'RETURN_REQUESTED', 'RETURNED')
        ),
    CONSTRAINT chk_orders_payment_status CHECK (
        payment_status IN ('PENDING', 'SUBMITTED', 'VERIFIED', 'FAILED', 'REFUNDED')
        ),
    CONSTRAINT chk_orders_total CHECK (total_amount >= 0)
);

CREATE INDEX idx_orders_user_id        ON orders (user_id);
CREATE INDEX idx_orders_status         ON orders (status);
CREATE INDEX idx_orders_payment_status ON orders (payment_status);
CREATE INDEX idx_orders_created_at     ON orders (created_at DESC);

-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE order_items
(
    id               BIGSERIAL PRIMARY KEY,
    order_id         BIGINT          NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    variant_id       BIGINT          REFERENCES product_variants (id) ON DELETE SET NULL,
    product_name     VARCHAR(200)    NOT NULL,
    size             VARCHAR(20)     NOT NULL,
    color            VARCHAR(50),
    sku              VARCHAR(100)    NOT NULL,
    quantity         INTEGER         NOT NULL,
    unit_price       NUMERIC(10, 2)  NOT NULL,
    line_total       NUMERIC(10, 2)  NOT NULL,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP WITH TIME ZONE,
    created_by       VARCHAR(100),
    updated_by       VARCHAR(100),

    CONSTRAINT chk_order_items_quantity CHECK (quantity > 0)
);

CREATE INDEX idx_order_items_order_id   ON order_items (order_id);
CREATE INDEX idx_order_items_variant_id ON order_items (variant_id);