package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("stock_levels")
public class StockLevelEntity {
    @Id
    private UUID id;

    @Column("organization_id")
    private UUID organizationId;

    @Column("product_id")
    private UUID productId;

    @Column("agency_id")
    private UUID agencyId;

    private Integer quantity;

    @Column("reserved_quantity")
    private Integer reservedQuantity;

    @Column("last_updated")
    private Instant lastUpdated;
}