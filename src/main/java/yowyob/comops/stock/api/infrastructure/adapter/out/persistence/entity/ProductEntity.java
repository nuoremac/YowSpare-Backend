package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class ProductEntity {
    @Id
    private UUID id;

    @Column("organization_id")
    private UUID organizationId;

    private String sku;
    private String name;
    private String description;

    @Column("category_id")
    private UUID categoryId;

    private String unit;

    @Column("is_stockable")
    private boolean isStockable;

    @Column("is_perishable")
    private boolean isPerishable;

    @Column("default_sale_price")
    private BigDecimal defaultSalePrice;

    @Column("default_cost_price")
    private BigDecimal defaultCostPrice;

    @Column("min_stock_level")
    private Integer minStockLevel;

    @Column("max_stock_level")
    private Integer maxStockLevel;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}