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
@Table("product_categories")
public class CategoryEntity {
    @Id
    private UUID id;

    @Column("organization_id")
    private UUID organizationId;

    private String name;
    private String description;

    @Column("parent_id")
    private UUID parentId;
}