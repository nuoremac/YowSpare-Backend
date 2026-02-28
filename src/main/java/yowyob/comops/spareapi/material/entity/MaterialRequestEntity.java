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
@Table("material_requests")
public class MaterialRequestEntity implements Persistable<UUID> {
    @Id
    private UUID id;

    @Transient
    private boolean newEntity;

    @Column("tenant_id")
    private UUID tenantId;

    @Column("agency_id")
    private UUID agencyId;

    @Column("department_id")
    private UUID departmentId;

    private String status;

    @Column("reason_code")
    private String reasonCode;

    @Column("reason_text")
    private String reasonText;

    @Column("requested_by")
    private String requestedBy;

    @Column("approved_by")
    private String approvedBy;

    @Column("approved_at")
    private Instant approvedAt;

    @Column("issued_by")
    private String issuedBy;

    @Column("issued_at")
    private Instant issuedAt;

    @Column("expected_return_at")
    private Instant expectedReturnAt;

    @Column("closed_by")
    private String closedBy;

    @Column("closed_at")
    private Instant closedAt;

    @Column("close_reason")
    private String closeReason;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @Override
    public boolean isNew() {
        return newEntity;
    }
}
