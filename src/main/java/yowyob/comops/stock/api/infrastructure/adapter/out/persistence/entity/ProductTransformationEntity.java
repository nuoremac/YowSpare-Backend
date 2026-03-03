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
@Table("product_transformations")
public class ProductTransformationEntity {
    @Id
    private UUID id;
    
    @Column("organization_id")
    private UUID organizationId;
    
    @Column("agency_id")
    private UUID agencyId;
    
    private String reference;
    private String status;
    private String description;
    private Instant date;
    
    @Column("created_at")
    private Instant createdAt;
    
    @Column("updated_at")
    private Instant updatedAt;
}