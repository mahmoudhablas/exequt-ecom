CREATE TABLE payments
(
    id           BIGSERIAL      PRIMARY KEY,
    order_id     BIGINT         NOT NULL,
    provider     VARCHAR(50)    NOT NULL,
    provider_ref VARCHAR(255),
    method       VARCHAR(50) NOT NULL,
    amount       DECIMAL(10, 2) NOT NULL,
    status       VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    paid_at      TIMESTAMP,
    created_at   TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP      NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_payments_order
        FOREIGN KEY (order_id)
        REFERENCES orders (id)
        ON DELETE RESTRICT,       -- never delete an order that has a payment

    CONSTRAINT uq_payments_provider_ref
        UNIQUE (provider, provider_ref),  -- idempotency — no duplicate transactions

    CONSTRAINT chk_payments_amount
        CHECK (amount > 0),

    CONSTRAINT chk_payments_paid_at
        CHECK (
            (status = 'SUCCESS' AND paid_at IS NOT NULL) OR
            (status <> 'SUCCESS')
        )                         -- paid_at must be set when payment succeeds
);

CREATE INDEX idx_payments_order_id     ON payments (order_id);
CREATE INDEX idx_payments_status       ON payments (status);
CREATE INDEX idx_payments_provider_ref ON payments (provider, provider_ref);