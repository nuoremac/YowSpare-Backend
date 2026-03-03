package yowyob.comops.api.domain.model.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationMember {
    private UUID id;
    private UUID organizationId;
    
    // Identité Utilisateur
    private UUID userId;
    private String userEmail;
    private String userFirstName;
    private String userLastName;

    // Contexte Organisationnel
    private UUID agencyId; // Null = Global (Siège)
    private String agencyName;

    // Rôle
    private UUID roleId;
    private String roleName;

    private boolean isActive;
    private Instant joinedAt;
}