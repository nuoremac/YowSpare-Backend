package yowyob.comops.spareapi.workflow.entity;

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
@Table("workflow_requests")
public class WorkflowRequestEntity implements Persistable<UUID> {
    @Id
    private UUID id;

    @Transient
    private boolean newEntity;

    @Column("tenant_id")
    private UUID tenantId;

    private String type; // RESERVATION, STOCK_ADJUSTMENT, REORDER, ...

    private String status; // PENDING, APPROVED, REJECTED, CANCELLED

    @Column("requested_by")
    private String requestedBy;

    @Column("approved_by")
    private String approvedBy;

    @Column("approved_at")
    private Instant approvedAt;

    @Column("payload_json")
    private String payloadJson;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @Override
    public boolean isNew() {
        return newEntity;
    }
}
