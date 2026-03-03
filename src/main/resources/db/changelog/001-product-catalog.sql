-- liquibase formatted sql

-- changeset your.name:stock-api-1-product-catalog
CREATE TABLE IF NOT EXISTS product_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    parent_id UUID,
    
    CONSTRAINT uq_stock_category_name_org UNIQUE (name, organization_id)
);

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    
    sku VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    
    category_id UUID REFERENCES product_categories(id) ON DELETE SET NULL,
    
    unit VARCHAR(20) DEFAULT 'PCS',
    is_stockable BOOLEAN DEFAULT true,
    is_perishable BOOLEAN DEFAULT false,
    
    default_sale_price NUMERIC(15, 2),
    default_cost_price NUMERIC(15, 2),
    
    min_stock_level INT,
    max_stock_level INT,
    
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    
    CONSTRAINT uq_stock_sku_org UNIQUE (sku, organization_id)
);

-- rollback DROP TABLE IF EXISTS products;
-- rollback DROP TABLE IF EXISTS product_categories;