package yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("agencies")
public class AgencyEntity {
    @Id
    private UUID id;
    @Column("organization_id")
    private UUID organizationId;
    private String code;
    private String name;
    @Column("short_name")
    private String shortName;
    @Column("long_name")
    private String longName;

    private String type; // WAREHOUSE, POS, etc.

    // Geo
    private String location;
    private String address;
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
    private String timezone;

    // Roles
    @Column("owner_id")
    private UUID ownerId;
    @Column("manager_id")
    private UUID managerId;
    private Boolean transferable;

    // Flags
    @Column("is_headquarter")
    private boolean isHeadquarter;
    @Column("is_active")
    private boolean isActive;
    @Column("is_public")
    private Boolean isPublic;
    @Column("is_business")
    private Boolean isBusiness;
    @Column("is_individual_business")
    private Boolean isIndividualBusiness;

    // Media
    @Column("logo_uri")
    private String logoUri;
    @Column("logo_id")
    private UUID logoId;

    // Contacts
    private String phone;
    private String email;
    private String whatsapp;
    @Column("social_network")
    private String socialNetwork;

    // Descriptions
    @Column("greeting_message")
    private String greetingMessage;
    private String description;
    @Column("open_time")
    private String openTime;
    @Column("close_time")
    private String closeTime;

    // Legal
    @Column("average_revenue")
    private BigDecimal averageRevenue;
    @Column("capital_share")
    private BigDecimal capitalShare;
    @Column("registration_number")
    private String registrationNumber;
    @Column("tax_number")
    private String taxNumber;

    // Meta
    private String keywords;
    @Column("total_affiliated_customers")
    private Integer totalAffiliatedCustomers;

    @Column("created_at")
    private Instant createdAt;
    @Column("updated_at")
    private Instant updatedAt;
}