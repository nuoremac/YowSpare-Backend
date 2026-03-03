-- liquibase formatted sql

-- changeset your.name:stock-api-3-stock-movements
CREATE TABLE IF NOT EXISTS stock_movements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    reference VARCHAR(50),
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    date TIMESTAMPTZ NOT NULL,
    source_agency_id UUID,
    destination_agency_id UUID,
    third_party_id UUID,
    notes TEXT,
    created_by UUID,
    validated_by UUID,
    validated_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS stock_movement_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    stock_movement_id UUID NOT NULL REFERENCES stock_movements(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id),
    quantity INT NOT NULL,
    cost_price NUMERIC(15, 2)
);

-- rollback DROP TABLE IF EXISTS stock_movement_items;
-- rollback DROP TABLE IF EXISTS stock_movements;