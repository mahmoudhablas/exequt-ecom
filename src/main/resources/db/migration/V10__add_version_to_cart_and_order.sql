-- V6__add_version_to_cart_and_order.sql
-- Add version column for optimistic locking

ALTER TABLE carts ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE orders ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

