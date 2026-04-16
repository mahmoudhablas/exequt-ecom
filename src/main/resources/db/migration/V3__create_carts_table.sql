CREATE TYPE cart_status AS ENUM ('ACTIVE', 'CHECKED_OUT', 'ABANDONED');

CREATE TABLE carts
(
    id          BIGSERIAL   PRIMARY KEY,
    customer_id BIGINT      NOT NULL,
    status      cart_status NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    expires_at  TIMESTAMP,

    CONSTRAINT fk_carts_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers (id)
        ON DELETE CASCADE
);

-- Only one ACTIVE cart allowed per customer at a time
CREATE UNIQUE INDEX idx_carts_one_active_per_customer
    ON carts (customer_id)
    WHERE status = 'ACTIVE';

CREATE INDEX idx_carts_customer_id ON carts (customer_id);
CREATE INDEX idx_carts_status      ON carts (status);
CREATE INDEX idx_carts_expires_at  ON carts (expires_at)
    WHERE expires_at IS NOT NULL;