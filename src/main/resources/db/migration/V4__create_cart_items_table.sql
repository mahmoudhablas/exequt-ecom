CREATE TABLE cart_items
(
    id         BIGSERIAL      PRIMARY KEY,
    cart_id    BIGINT         NOT NULL,
    product_id BIGINT         NOT NULL,
    quantity   INT            NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    added_at   TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP      NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_cart_items_cart
        FOREIGN KEY (cart_id)
        REFERENCES carts (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_cart_items_product
            FOREIGN KEY (product_id)
            REFERENCES products (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_cart_items_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_cart_items_unit_price
        CHECK (unit_price >= 0),

    -- Prevent duplicate product rows in the same cart
    CONSTRAINT uq_cart_items_cart_product
        UNIQUE (cart_id, product_id)
);

CREATE INDEX idx_cart_items_cart_id    ON cart_items (cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items (product_id);