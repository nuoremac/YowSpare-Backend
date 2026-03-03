package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("stock_movement_items")
public class StockMovementItemEntity {
    @Id
    private UUID id;
    
    @Column("stock_movement_id")
    private UUID stockMovementId;

    @Column("product_id")
    private UUID productId;

    private Integer quantity;

    @Column("cost_price")
    private BigDecimal costPrice;
}