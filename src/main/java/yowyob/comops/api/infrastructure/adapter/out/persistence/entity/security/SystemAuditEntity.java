package yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("system_audits")
public class SystemAuditEntity {
    @Id
    private UUID id;

    @Column("organization_id")
    private UUID organizationId;

    @Column("user_id")
    private UUID userId;

    @Column("user_email")
    private String userEmail;

    private String action;
    private String details;

    @Column("ip_address")
    private String ipAddress;

    @Column("created_at")
    private Instant createdAt;
}
