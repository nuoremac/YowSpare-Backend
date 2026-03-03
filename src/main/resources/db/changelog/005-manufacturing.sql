-- liquibase formatted sql

-- changeset your.name:stock-api-5-manufacturing
CREATE TABLE IF NOT EXISTS product_transformations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    agency_id UUID NOT NULL,
    reference VARCHAR(50),
    status VARCHAR(50),
    description TEXT,
    date TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS transformation_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transformation_id UUID NOT NULL REFERENCES product_transformations(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id),
    type VARCHAR(20) NOT NULL, -- INPUT or OUTPUT
    quantity INT NOT NULL
);

-- rollback DROP TABLE IF EXISTS transformation_items;
-- rollback DROP TABLE IF EXISTS product_transformations;