-- While we have kept to the Northwind schema as much as possible we have to extend it with these
-- columns in order to get basic functionality needed for our demonstration

-- customers
ALTER TABLE customers
    ADD COLUMN created_at  TIMESTAMPTZ NOT NULL DEFAULT '1970-01-01 00:00:00+00',
    ADD COLUMN created_by  VARCHAR(100) NOT NULL DEFAULT 'System',
    ADD COLUMN updated_at  TIMESTAMPTZ NOT NULL DEFAULT '1970-01-01 00:00:00+00',
    ADD COLUMN updated_by  VARCHAR(100) NOT NULL DEFAULT 'System';

-- orders
ALTER TABLE orders
    ADD COLUMN created_at  TIMESTAMPTZ NOT NULL DEFAULT '1970-01-01 00:00:00+00',
    ADD COLUMN created_by  VARCHAR(100) NOT NULL DEFAULT 'System',
    ADD COLUMN updated_at  TIMESTAMPTZ NOT NULL DEFAULT '1970-01-01 00:00:00+00',
    ADD COLUMN updated_by  VARCHAR(100) NOT NULL DEFAULT 'System';

-- order_details
ALTER TABLE order_details
    ADD COLUMN created_at  TIMESTAMPTZ NOT NULL DEFAULT '1970-01-01 00:00:00+00',
    ADD COLUMN created_by  VARCHAR(100) NOT NULL DEFAULT 'System',
    ADD COLUMN updated_at  TIMESTAMPTZ NOT NULL DEFAULT '1970-01-01 00:00:00+00',
    ADD COLUMN updated_by  VARCHAR(100) NOT NULL DEFAULT 'System';


ALTER TABLE customers
    ALTER COLUMN created_at  DROP DEFAULT,
ALTER COLUMN created_by  DROP DEFAULT,
    ALTER COLUMN updated_at  DROP DEFAULT,
    ALTER COLUMN updated_by  DROP DEFAULT;

ALTER TABLE orders
    ALTER COLUMN created_at  DROP DEFAULT,
ALTER COLUMN created_by  DROP DEFAULT,
    ALTER COLUMN updated_at  DROP DEFAULT,
    ALTER COLUMN updated_by  DROP DEFAULT;

ALTER TABLE order_details
    ALTER COLUMN created_at  DROP DEFAULT,
ALTER COLUMN created_by  DROP DEFAULT,
    ALTER COLUMN updated_at  DROP DEFAULT,
    ALTER COLUMN updated_by  DROP DEFAULT;