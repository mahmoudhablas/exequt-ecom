ALTER TABLE orders
ALTER COLUMN shipping_addr TYPE VARCHAR(200)
USING shipping_addr::text;