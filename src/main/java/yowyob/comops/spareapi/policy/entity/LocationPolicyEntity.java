package yowyob.comops.spareapi.policy.entity;

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
@Table("location_policies")
public class LocationPolicyEntity implements Persistable<UUID> {
    @Id
    private UUID id;

    @Transient
    private boolean newEntity;

    @Column("tenant_id")
    private UUID tenantId;

    @Column("agency_id")
    private UUID agencyId;

    @Column("bin_code")
    private String binCode;

    @Column("product_id")
    private UUID productId; // null => applies to all products in this bin

    @Column("min_qty")
    private Integer minQty;

    @Column("max_qty")
    private Integer maxQty;

    @Column("safety_stock")
    private Integer safetyStock;

    @Column("reorder_point")
    private Integer reorderPoint;

    @Column("cycle_count_days")
    private Integer cycleCountDays;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @Override
    public boolean isNew() {
        return newEntity;
    }
}
