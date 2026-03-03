package yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("organizations")
public class OrganizationEntity {
    @Id
    private UUID id;
    
    @Column("business_actor_id")
    private UUID businessActorId; // L'ID du propriétaire (lié au User)

    private String code;
    private String name;

    @Column("service_type")
    private String serviceType;

    @Column("is_individual_business")
    private boolean isIndividualBusiness;

    private String status;

    @Column("manager_id")
    private UUID managerId;

    private String email;
    private String description;

    @Column("logo_uri")
    private String logoUri;

    @Column("logo_id")
    private UUID logoId;

    @Column("website_url")
    private String websiteUrl;

    @Column("social_network")
    private String socialNetwork;

    @Column("business_registration_number")
    private String businessRegistrationNumber;

    @Column("tax_number")
    private String taxNumber;

    @Column("capital_share")
    private Double capitalShare;

    @Column("ceo_name")
    private String ceoName;

    @Column("year_founded")
    private Integer yearFounded;

    @Column("founding_date")
    private LocalDate foundingDate;

    @Column("legal_form")
    private String legalForm;

    private String keywords;

    @Column("number_of_employees")
    private Integer numberOfEmployees;

    @Column("is_active")
    private boolean isActive;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
