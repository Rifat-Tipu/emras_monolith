-- ─────────────────────────────────────────────────────────────────────────────
-- V8: Reviews and Wishlists
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE reviews
(
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    product_id        BIGINT      NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    rating            SMALLINT    NOT NULL,
    comment           TEXT,
    verified_purchase BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP WITH TIME ZONE,
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100),

    CONSTRAINT chk_reviews_rating        CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT uq_reviews_user_product   UNIQUE (user_id, product_id)
);

CREATE INDEX idx_reviews_product_id ON reviews (product_id);
CREATE INDEX idx_reviews_user_id    ON reviews (user_id);
CREATE INDEX idx_reviews_rating     ON reviews (rating);

-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE wishlists
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    variant_id  BIGINT      NOT NULL REFERENCES product_variants (id) ON DELETE CASCADE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),

    CONSTRAINT uq_wishlists_user_variant UNIQUE (user_id, variant_id)
);

CREATE INDEX idx_wishlists_user_id ON wishlists (user_id);