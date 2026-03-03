package yowyob.comops.api.domain.model.thirdparty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdParty {
    private UUID id;
    private UUID tenantId;
    private UUID agencyId;
    private String code;

    // Basic Info
    private String name;
    private String shortName;
    private String description;
    
    // Accounting
    private String accountingAccount;
    private String bankAccountNumber; // Compte Bancaire (IBAN/RIB)

    // Legal
    private String taxNumber; // NUI / Numero Fiscal
    private String tradeRegistryNumber; // Registre Commerce
    private String vatNumber;
    
    private ThirdPartyType type;
    private BusinessSector businessSector; // SecteurActivite (enum?)
    private CompanySize companySize; // TailleEntreprise

    // Contact
    private String email;
    private String phoneNumber;
    private String website;
    private PreferredChannel preferredChannel; // CanalPrefere

    // Address
    private String address;
    private String addressComplement;
    private String postalCode;
    private String city;
    private String country;

    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
