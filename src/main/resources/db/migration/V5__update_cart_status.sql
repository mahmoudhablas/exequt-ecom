-- 1. Drop dependent index
DROP INDEX IF EXISTS idx_carts_one_active_per_customer;

-- 2. Drop default
ALTER TABLE carts ALTER COLUMN status DROP DEFAULT;

-- 3. Convert ENUM → VARCHAR
ALTER TABLE carts
ALTER COLUMN status TYPE VARCHAR(20)
USING status::text;

-- 4. Restore default
ALTER TABLE carts
ALTER COLUMN status SET DEFAULT 'ACTIVE';

-- 5. Recreate index
CREATE UNIQUE INDEX idx_carts_one_active_per_customer
ON carts (customer_id)
WHERE status = 'ACTIVE';

-- 6. Drop enum type
DROP TYPE IF EXISTS cart_status;