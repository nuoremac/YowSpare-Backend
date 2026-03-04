package yowyob.comops.api.domain.model.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppBusinessSettings {
    private UUID id;
    private UUID organizationId;
    
    // -- Identification --
    private String organizationPrefix; // ex: "YOW" (Global pour tous les docs de l'org)

    // -- Règles Commerciales --
    private boolean negotiateSellingPrice;
    private boolean sellingPriceIncludeVat;
    private boolean authorizeExceptionalDiscount;
    private Double grantableDiscountRate;

    // -- Facturation (Séquences) --
    private Integer lengthOfVatInvoiceNumber;
    private String prefixOfVatInvoiceNumber; // Surcharge possible pour les factures ("INV")

    // -- Alertes Globales --
    private boolean lowStockAlert;
    private boolean preventiveMaintenanceAlert;
}
