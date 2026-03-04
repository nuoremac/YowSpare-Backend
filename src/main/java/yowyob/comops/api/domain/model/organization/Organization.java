package yowyob.comops.api.domain.model.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization {
    private UUID id;
    private UUID businessActorId; // Owner (Entité Légal)
    
    // Identification
    private String code;
    private String name;
    private String serviceType;
    private boolean isIndividualBusiness;
    private String status; // ACTIVE, INACTIVE, SUSPENDED

    // Management
    private UUID managerId; // Le CEO ou Gérant principal déclaré

    // Contact & Media
    private String email;
    private String description;
    private String logoUri;
    private UUID logoId;
    private String websiteUrl;
    private String socialNetwork; // JSON String

    // Legal
    private String businessRegistrationNumber;
    private String taxNumber;
    private Double capitalShare;
    private String ceoName;
    private Integer yearFounded;
    private LocalDate foundingDate;
    private String legalForm;

    // Meta
    private String keywords;
    private Integer numberOfEmployees;

    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
