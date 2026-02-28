package yowyob.comops.spareapi.supplier.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("supplier_products")
public class SupplierProductEntity implements Persistable<UUID> {
    @Id
    private UUID id;

    @Transient
    private boolean newEntity;

    @Column("tenant_id")
    private UUID tenantId;

    @Column("supplier_id")
    private UUID supplierId;

    @Column("product_id")
    private UUID productId;

    @Column("lead_time_days")
    private Integer leadTimeDays;

    private Integer moq;

    private Boolean preferred;

    @Column("unit_price")
    private BigDecimal unitPrice;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @Override
    public boolean isNew() {
        return newEntity;
    }
}
