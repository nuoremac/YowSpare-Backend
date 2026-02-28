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
@Table("trace_events")
public class TraceEventEntity implements Persistable<UUID> {
    @Id
    private UUID id;

    @Transient
    private boolean newEntity;

    @Column("tenant_id")
    private UUID tenantId;

    @Column("entity_type")
    private String entityType;

    @Column("entity_id")
    private UUID entityId;

    @Column("event_type")
    private String eventType;

    @Column("actor_id")
    private String actorId;

    @Column("agency_id")
    private UUID agencyId;

    @Column("department_id")
    private UUID departmentId;

    @Column("payload_json")
    private String payloadJson;

    @Column("created_at")
    private Instant createdAt;

    @Override
    public boolean isNew() {
        return newEntity;
    }
}
