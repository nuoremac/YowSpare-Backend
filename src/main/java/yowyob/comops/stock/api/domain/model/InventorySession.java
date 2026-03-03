package yowyob.comops.stock.api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySession {
    private UUID id;
    private UUID organizationId;
    private UUID agencyId;
    
    private String reference; // INV-2025-001
    private String description;
    private InventoryStatus status;
    
    private Instant startDate;
    private Instant validatedDate;
    private UUID validatedBy;

    // Portée (Optionnel : si null, tout l'entrepôt)
    private UUID categoryIdScope; 

    private List<InventoryCount> counts;

    public enum InventoryStatus {
        OPEN,       // En cours de comptage
        REVIEW,     // Comptage fini, en attente de validation
        VALIDATED,  // Stocks ajustés, session close
        CANCELLED
    }
}