package yowyob.comops.api.domain.model.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agency {
    private UUID id;
    private UUID organizationId;
    
    // Identification
    private String code;
    private String name;
    private String shortName;
    private String longName;
    private String type; // WAREHOUSE, POS, OFFICE, HQ

    // Localisation
    private String location; // Nom du lieu-dit
    private String address;
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
    private String timezone;

    // Management
    private UUID ownerId;
    private UUID managerId;
    private Boolean transferable;

    // Status & Flags
    private Boolean isHeadquarter;
    private Boolean isActive;
    private Boolean isPublic;
    private Boolean isBusiness; // Est un point de vente ?
    private Boolean isIndividualBusiness;

    // Media & Branding
    private String logoUri;
    private UUID logoId;

    // Contact
    private String phone;
    private String email;
    private String whatsapp;
    private String socialNetwork; // JSON String

    // Info Commerciale
    private String greetingMessage;
    private String description;
    private String openTime;
    private String closeTime;

    // Legal & Financial
    private BigDecimal averageRevenue;
    private BigDecimal capitalShare;
    private String registrationNumber;
    private String taxNumber;

    // Stats & Meta
    private List<String> keywords;
    private Integer totalAffiliatedCustomers;

    // Relations (Contexte Géographique)
    @Builder.Default
    private List<PointOfInterest> nearbyPoints = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;
}