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
@Table("agency_settings")
public class AgencySettingsEntity {
    @Id
    private UUID id;
    @Column("agency_id")
    private UUID agencyId;

    @Column("agency_prefix")
    private String agencyPrefix; // Nouveau champ

    // Printing
    @Column("is_print_logo")
    private Boolean isPrintLogo;

    @Column("paper_format")
    private String paperFormat;

    @Column("ticket_footer_message")
    private String ticketFooterMessage;

    // Local Rules
    @Column("allow_negative_stock")
    private Boolean allowNegativeStock;

    @Column("default_timezone")
    private String defaultTimezone;

    @Column("updated_at")
    private Instant updatedAt;
}
