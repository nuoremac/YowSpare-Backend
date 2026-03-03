-- liquibase formatted sql

-- changeset your.name:stock-api-2-stock-levels
CREATE TABLE IF NOT EXISTS stock_levels (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    agency_id UUID NOT NULL,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    
    quantity INT NOT NULL DEFAULT 0,
    reserved_quantity INT DEFAULT 0,
    last_updated TIMESTAMPTZ DEFAULT NOW(),
    
    CONSTRAINT uq_stock_level_product_agency UNIQUE (product_id, agency_id)
);

CREATE INDEX idx_stock_level_org ON stock_levels(organization_id);
CREATE INDEX idx_stock_level_agency ON stock_levels(agency_id);

-- rollback DROP INDEX IF EXISTS idx_stock_level_agency;
-- rollback DROP INDEX IF EXISTS idx_stock_level_org;
-- rollback DROP TABLE IF EXISTS stock_levels;