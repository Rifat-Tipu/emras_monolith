-- ─────────────────────────────────────────────────────────────────────────────
-- V2: Categories (self-referencing for sub-categories)
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE categories
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100)        NOT NULL,
    slug          VARCHAR(120)        NOT NULL UNIQUE,
    description   TEXT,
    parent_id     BIGINT              REFERENCES categories (id) ON DELETE SET NULL,
    gender_target VARCHAR(20)         NOT NULL DEFAULT 'MEN',
    image_url     VARCHAR(500),
    is_active     BOOLEAN             NOT NULL DEFAULT TRUE,
    sort_order    INTEGER             NOT NULL DEFAULT 0,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP WITH TIME ZONE,
    created_by    VARCHAR(100),
    updated_by    VARCHAR(100),

    CONSTRAINT chk_categories_gender CHECK (gender_target IN ('MEN', 'WOMEN', 'CHILDREN', 'UNISEX'))
);

CREATE INDEX idx_categories_parent_id     ON categories (parent_id);
CREATE INDEX idx_categories_gender_target ON categories (gender_target);
CREATE INDEX idx_categories_is_active     ON categories (is_active);