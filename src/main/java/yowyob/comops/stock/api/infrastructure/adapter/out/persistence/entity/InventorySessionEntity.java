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
@Table("inventory_sessions")
public class InventorySessionEntity {
    @Id
    private UUID id;
    @Column("organization_id")
    private UUID organizationId;
    @Column("agency_id")
    private UUID agencyId;

    private String reference;
    private String description;
    private String status;

    @Column("start_date")
    private Instant startDate;
    @Column("validated_date")
    private Instant validatedDate;
    @Column("validated_by")
    private UUID validatedBy;

    @Column("category_id_scope")
    private UUID categoryIdScope;
}