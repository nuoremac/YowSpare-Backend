-- liquibase formatted sql

-- changeset your.name:stock-api-4-inventory-sessions
CREATE TABLE IF NOT EXISTS inventory_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    agency_id UUID NOT NULL,
    reference VARCHAR(50),
    description TEXT,
    status VARCHAR(50),
    start_date TIMESTAMPTZ,
    validated_date TIMESTAMPTZ,
    validated_by UUID,
    category_id_scope UUID
);

CREATE TABLE IF NOT EXISTS inventory_counts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES inventory_sessions(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id),
    theoretical_quantity INT,
    physical_quantity INT,
    variance INT
);

-- rollback DROP TABLE IF EXISTS inventory_counts;
-- rollback DROP TABLE IF EXISTS inventory_sessions;```