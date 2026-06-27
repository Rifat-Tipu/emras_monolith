-- ─────────────────────────────────────────────────────────────────────────────
-- V4: Cart and Cart Items
-- session_id supports guest carts (not logged in)
-- user_id is set once the guest logs in (cart merge)
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE cart
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT      REFERENCES users (id) ON DELETE CASCADE,
    session_id  VARCHAR(100),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),

    CONSTRAINT chk_cart_owner CHECK (user_id IS NOT NULL OR session_id IS NOT NULL)
);

CREATE UNIQUE INDEX idx_cart_user_id    ON cart (user_id)    WHERE user_id IS NOT NULL;
CREATE UNIQUE INDEX idx_cart_session_id ON cart (session_id) WHERE session_id IS NOT NULL;

-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE cart_items
(
    id          BIGSERIAL PRIMARY KEY,
    cart_id     BIGINT          NOT NULL REFERENCES cart (id) ON DELETE CASCADE,
    variant_id  BIGINT          NOT NULL REFERENCES product_variants (id) ON DELETE CASCADE,
    quantity    INTEGER         NOT NULL DEFAULT 1,
    unit_price  NUMERIC(10, 2)  NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),

    CONSTRAINT chk_cart_items_quantity CHECK (quantity > 0),
    CONSTRAINT uq_cart_items_cart_variant UNIQUE (cart_id, variant_id)
);

CREATE INDEX idx_cart_items_cart_id    ON cart_items (cart_id);
CREATE INDEX idx_cart_items_variant_id ON cart_items (variant_id);