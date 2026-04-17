CREATE TABLE orders
(
    id              BIGSERIAL      PRIMARY KEY,
    customer_id     BIGINT         NOT NULL,
    cart_id         BIGINT,
    status          VARCHAR(20)   NOT NULL DEFAULT 'CONFIRMED',
    subtotal        DECIMAL(10, 2) NOT NULL,
    tax             DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total           DECIMAL(10, 2) NOT NULL,
    shipping_addr   JSONB          NOT NULL,
    created_at      TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP      NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_orders_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers (id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_orders_cart
        FOREIGN KEY (cart_id)
        REFERENCES carts (id)
        ON DELETE SET NULL,       -- keep order history even if cart is deleted

    CONSTRAINT chk_orders_subtotal
        CHECK (subtotal >= 0),

    CONSTRAINT chk_orders_tax
        CHECK (tax >= 0),

    CONSTRAINT chk_orders_total
        CHECK (total >= 0),

    CONSTRAINT chk_orders_total_matches
        CHECK (total = subtotal + tax)
);

CREATE INDEX idx_orders_customer_id ON orders (customer_id);
CREATE INDEX idx_orders_cart_id     ON orders (cart_id);
CREATE INDEX idx_orders_status      ON orders (status);
CREATE INDEX idx_orders_created_at  ON orders (created_at DESC);

-- Composite for "my orders" query — customer + recent first
CREATE INDEX idx_orders_customer_created
    ON orders (customer_id, created_at DESC);
