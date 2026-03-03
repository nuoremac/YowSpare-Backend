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
@Table("stock_movements")
public class StockMovementEntity {
    @Id
    private UUID id;
    @Column("organization_id")
    private UUID organizationId;

    private String reference;
    private String type; // IN, OUT, TRANSFER, ADJUSTMENT
    private String status; // DRAFT, VALIDATED, CANCELLED
    private Instant date;

    @Column("source_agency_id")
    private UUID sourceAgencyId;

    @Column("destination_agency_id")
    private UUID destinationAgencyId;

    @Column("third_party_id")
    private UUID thirdPartyId;

    private String notes;

    @Column("created_by")
    private UUID createdBy;

    @Column("validated_by")
    private UUID validatedBy;

    @Column("validated_at")
    private Instant validatedAt;

    @Column("created_at")
    private Instant createdAt;
}