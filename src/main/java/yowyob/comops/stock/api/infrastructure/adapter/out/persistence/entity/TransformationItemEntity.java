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
@Table("transformation_items")
public class TransformationItemEntity {
    @Id
    private UUID id;
    @Column("transformation_id")
    private UUID transformationId;
    @Column("product_id")
    private UUID productId;
    private String type; // INPUT, OUTPUT
    private Integer quantity;
}