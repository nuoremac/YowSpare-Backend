package yowyob.comops.spareapi.material.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("material_request_items")
public class MaterialRequestItemEntity implements Persistable<UUID> {
    @Id
    private UUID id;

    @Transient
    private boolean newEntity;

    @Column("tenant_id")
    private UUID tenantId;

    @Column("request_id")
    private UUID requestId;

    @Column("product_id")
    private UUID productId;

    @Column("quantity_requested")
    private Integer quantityRequested;

    @Column("quantity_issued")
    private Integer quantityIssued;

    @Column("quantity_returned")
    private Integer quantityReturned;

    private String note;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @Override
    public boolean isNew() {
        return newEntity;
    }
}
