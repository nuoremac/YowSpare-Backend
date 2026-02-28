package yowyob.comops.spareapi.analytics.entity;

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
@Table("analytics_recommendations")
public class AnalyticsRecommendationEntity implements Persistable<UUID> {
    @Id
    private UUID id;

    @Transient
    private boolean newEntity;

    @Column("tenant_id")
    private UUID tenantId;

    @Column("agency_id")
    private UUID agencyId;

    @Column("product_id")
    private UUID productId;

    @Column("avg_daily_usage")
    private BigDecimal avgDailyUsage;

    @Column("days_of_cover")
    private BigDecimal daysOfCover;

    @Column("abc_class")
    private String abcClass;

    @Column("stockout_risk")
    private BigDecimal stockoutRisk;

    @Column("suggested_reorder_qty")
    private Integer suggestedReorderQty;

    @Column("computed_at")
    private Instant computedAt;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @Override
    public boolean isNew() {
        return newEntity;
    }
}
