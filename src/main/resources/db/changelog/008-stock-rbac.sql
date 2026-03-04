-- liquibase formatted sql

-- changeset your.name:8-stock-rbac

-- ==============================================================================
-- 1. CREATION DES NOUVEAUX ROLES
-- ==============================================================================
INSERT INTO roles (name, description) VALUES 
('ROLE_STOCK_MANAGER', 'Responsable de la gestion des stocks, du catalogue et des inventaires'),
('ROLE_WAREHOUSE_CLERK', 'Opérateur logistique : réceptions, expéditions et comptage'),
('ROLE_AUDITOR', 'Auditeur avec accès lecture seule sur toutes les données (y compris financières)')
ON CONFLICT (name) DO NOTHING;

-- ==============================================================================
-- 2. CREATION DES PERMISSIONS (Granularité Fine)
-- ==============================================================================

-- Catalogue Produit
INSERT INTO permissions (resource, action, description) VALUES 
('PRODUCT', 'READ', 'Voir la liste et les détails des produits'),
('PRODUCT', 'MANAGE', 'Créer, modifier et supprimer des produits et catégories');

-- Consultation Stock
INSERT INTO permissions (resource, action, description) VALUES 
('STOCK', 'READ', 'Consulter les niveaux de stock (quantités)'),
('STOCK', 'VALUATION_READ', 'Consulter la valeur financière du stock (PMP, Coûts)');

-- Mouvements (Entrées / Sorties / Transferts)
INSERT INTO permissions (resource, action, description) VALUES 
('MOVEMENT', 'READ', 'Voir l''historique des mouvements de stock'),
('MOVEMENT', 'CREATE', 'Saisir des mouvements en brouillon (Réception, Livraison, Transfert)'),
('MOVEMENT', 'VALIDATE', 'Valider définitivement les mouvements (Impact sur les quantités)');

-- Inventaires (Comptage)
INSERT INTO permissions (resource, action, description) VALUES 
('INVENTORY', 'INITIATE', 'Planifier et ouvrir une session d''inventaire'),
('INVENTORY', 'COUNT', 'Saisir les comptages physiques'),
('INVENTORY', 'VALIDATE', 'Analyser les écarts et valider l''inventaire');

-- Fabrication (Optionnel pour le futur)
INSERT INTO permissions (resource, action, description) VALUES 
('MANUFACTURING', 'MANAGE', 'Gérer les recettes et les ordres de fabrication')
ON CONFLICT (resource, action) DO NOTHING;


-- ==============================================================================
-- 3. ASSIGNATION DES PERMISSIONS AUX ROLES
-- ==============================================================================

-- A. ROLE_ADMIN (Dieu) & ROLE_MANAGER (Boss Local)
-- Ils héritent de TOUTES les permissions Stock
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name IN ('ROLE_ADMIN', 'ROLE_MANAGER')
  AND p.resource IN ('PRODUCT', 'STOCK', 'MOVEMENT', 'INVENTORY', 'MANUFACTURING')
ON CONFLICT DO NOTHING;


-- B. ROLE_STOCK_MANAGER (Chef Magasinier)
-- Accès TOTAL au métier Stock (y compris validation et prix)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_STOCK_MANAGER'
  AND p.resource IN ('PRODUCT', 'STOCK', 'MOVEMENT', 'INVENTORY', 'MANUFACTURING')
ON CONFLICT DO NOTHING;


-- C. ROLE_WAREHOUSE_CLERK (Magasinier / Manutentionnaire)
-- Accès Opérationnel Restreint :
-- - Peut voir les produits et le stock (quantité seulement)
-- - Peut créer des mouvements (réceptionner un camion)
-- - Peut compter (inventaire)
-- - NE PEUT PAS : Gérer le catalogue, Voir les prix (VALUATION), Valider (impacter le stock), Valider inventaire
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_WAREHOUSE_CLERK'
  AND (
       (p.resource = 'PRODUCT' AND p.action = 'READ') OR
       (p.resource = 'STOCK' AND p.action = 'READ') OR
       (p.resource = 'MOVEMENT' AND p.action IN ('READ', 'CREATE')) OR
       (p.resource = 'INVENTORY' AND p.action = 'COUNT')
  )
ON CONFLICT DO NOTHING;


-- D. ROLE_AUDITOR (Contrôleur)
-- Accès Lecture Seule TOTAL (y compris finance)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_AUDITOR'
  AND p.action LIKE '%READ%' -- Capture READ et VALUATION_READ
ON CONFLICT DO NOTHING;

-- rollback DELETE FROM role_permissions WHERE role_id IN (SELECT id FROM roles WHERE name IN ('ROLE_STOCK_MANAGER', 'ROLE_WAREHOUSE_CLERK', 'ROLE_AUDITOR'));
-- rollback DELETE FROM roles WHERE name IN ('ROLE_STOCK_MANAGER', 'ROLE_WAREHOUSE_CLERK', 'ROLE_AUDITOR');
-- rollback DELETE FROM permissions WHERE resource IN ('PRODUCT', 'STOCK', 'MOVEMENT', 'INVENTORY', 'MANUFACTURING');