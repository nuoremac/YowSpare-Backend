package yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization;

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
@Table("organization_members")
public class OrganizationMemberEntity {
    @Id
    private UUID id;
    @Column("organization_id")
    private UUID organizationId;
    @Column("user_id")
    private UUID userId;

    @Column("agency_id")
    private UUID agencyId; // Null si membre global

    @Column("role_id")
    private UUID roleId;

    @Column("is_active")
    private boolean isActive;

    @Column("joined_at")
    private Instant joinedAt;
}