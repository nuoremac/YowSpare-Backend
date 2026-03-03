package yowyob.comops.stock.api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private UUID id;
    private UUID organizationId;

    // Identification
    private String sku; // Stock Keeping Unit (Unique par org)
    private String name;
    private String description;
    private UUID categoryId;
    private String categoryName; // Enrichi

    // Caractéristiques
    private String unit; // pcs, kg, l
    private boolean isStockable; // true = Produit physique, false = Service
    private boolean isPerishable; // Date d'expiration requise ?

    // Prix de référence (Indicatif pour le catalogue)
    private BigDecimal defaultSalePrice;
    private BigDecimal defaultCostPrice;

    // Seuils
    private Integer minStockLevel; // Alerte stock bas
    private Integer maxStockLevel; // Alerte sur-stock

    private Instant createdAt;
    private Instant updatedAt;
}