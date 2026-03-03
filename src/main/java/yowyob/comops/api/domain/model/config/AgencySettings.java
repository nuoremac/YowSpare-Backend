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
public class AgencySettings {
    private UUID id;
    private UUID agencyId;
    
    // -- Identification --
    private String agencyPrefix; // ex: "DLA" (Spécifique à cette agence)

    // -- Impression & Ticket de Caisse --
    private boolean isPrintLogo;
    private String paperFormat;
    private String ticketFooterMessage;

    // -- Comportement Local --
    private boolean allowNegativeStock;
    private String defaultTimezone;
}
