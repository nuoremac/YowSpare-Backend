package yowyob.comops.api.infrastructure.adapter.out.persistence.entity.config;

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
@Table("app_business_settings")
public class AppBusinessSettingsEntity {
    @Id
    private UUID id;
    
    @Column("organization_id")
    private UUID organizationId;
    
    @Column("organization_prefix")
    private String organizationPrefix; // Nouveau champ

    // General
    @Column("negotiate_selling_price")
    private Boolean negotiateSellingPrice;

    @Column("selling_price_include_vat")
    private Boolean sellingPriceIncludeVat;

    @Column("authorize_exceptional_discount")
    private Boolean authorizeExceptionalDiscount;

    @Column("grantable_discount_rate")
    private Double grantableDiscountRate;

    // Invoicing
    @Column("length_of_vat_invoice_number")
    private Integer lengthOfVatInvoiceNumber;

    @Column("prefix_of_vat_invoice_number")
    private String prefixOfVatInvoiceNumber;

    // Alertes
    @Column("low_stock_alert")
    private Boolean lowStockAlert;

    @Column("preventive_maintenance_alert")
    private Boolean preventiveMaintenanceAlert;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}