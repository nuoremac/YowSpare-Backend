package yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security;

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
@Table("users")
public class UserEntity {
    @Id
    private UUID id;
    
    @Column("organization_id")
    private UUID organizationId;

    private String email;

    @Column("password_hash")
    private String passwordHash;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("business_actor_id")
    private UUID businessActorId;

    @Column("is_active")
    private boolean isActive;

    private String plan;

    @Column("onboarding_status")
    private String onboardingStatus;

    @Column("onboarding_step")
    private Integer onboardingStep;

    @Column("created_at")
    private Instant createdAt;
}