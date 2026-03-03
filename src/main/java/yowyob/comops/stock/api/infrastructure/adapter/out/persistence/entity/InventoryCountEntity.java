package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("inventory_counts")
public class InventoryCountEntity {
    @Id
    private UUID id;
    @Column("session_id")
    private UUID sessionId;
    @Column("product_id")
    private UUID productId;

    @Column("theoretical_quantity")
    private Integer theoreticalQuantity;
    @Column("physical_quantity")
    private Integer physicalQuantity;
    private Integer variance;
}