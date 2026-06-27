-- ─────────────────────────────────────────────────────────────────────────────
-- V3: Products, Product Images, Product Variants
-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE products
(
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(200)        NOT NULL,
    slug           VARCHAR(220)        NOT NULL UNIQUE,
    description    TEXT,
    category_id    BIGINT              NOT NULL REFERENCES categories (id) ON DELETE RESTRICT,
    brand          VARCHAR(100),
    price          NUMERIC(10, 2)      NOT NULL,
    discount_price NUMERIC(10, 2),
    is_featured    BOOLEAN             NOT NULL DEFAULT FALSE,
    is_active      BOOLEAN             NOT NULL DEFAULT TRUE,
    tags           TEXT,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP WITH TIME ZONE,
    created_by     VARCHAR(100),
    updated_by     VARCHAR(100),

    CONSTRAINT chk_products_price          CHECK (price >= 0),
    CONSTRAINT chk_products_discount_price CHECK (discount_price IS NULL OR discount_price >= 0)
);

CREATE INDEX idx_products_category_id ON products (category_id);
CREATE INDEX idx_products_is_featured ON products (is_featured);
CREATE INDEX idx_products_is_active   ON products (is_active);
CREATE INDEX idx_products_price       ON products (price);

-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE product_images
(
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT          NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    image_url   VARCHAR(500)    NOT NULL,
    alt_text    VARCHAR(200),
    sort_order  INTEGER         NOT NULL DEFAULT 0,
    is_primary  BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100)
);

CREATE INDEX idx_product_images_product_id ON product_images (product_id);

-- ─────────────────────────────────────────────────────────────────────────────

CREATE TABLE product_variants
(
    id             BIGSERIAL PRIMARY KEY,
    product_id     BIGINT          NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    size           VARCHAR(20)     NOT NULL,
    color          VARCHAR(50),
    sku            VARCHAR(100)    NOT NULL UNIQUE,
    stock_quantity INTEGER         NOT NULL DEFAULT 0,
    price_modifier NUMERIC(10, 2)  NOT NULL DEFAULT 0.00,
    is_active      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP WITH TIME ZONE,
    created_by     VARCHAR(100),
    updated_by     VARCHAR(100),

    CONSTRAINT chk_variants_stock CHECK (stock_quantity >= 0)
);

CREATE INDEX idx_product_variants_product_id ON product_variants (product_id);
CREATE INDEX idx_product_variants_sku        ON product_variants (sku);
CREATE INDEX idx_product_variants_is_active  ON product_variants (is_active);