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
public class StockMovement {
    private UUID id;
    private UUID organizationId;
    private String reference; // ex: IN-2025-001
    
    private MovementType type;
    private MovementStatus status;
    private Instant date;

    // Lieux concernés
    private UUID sourceAgencyId; // Pour OUT et TRANSFER
    private UUID destinationAgencyId; // Pour IN et TRANSFER
    
    // Tiers (optionnel)
    private UUID thirdPartyId; // Client ou Fournisseur

    private String notes;
    private UUID createdBy;
    private UUID validatedBy;
    private Instant validatedAt;

    private List<StockMovementItem> items;

    // --- ENUMS ---
    public enum MovementType {
        IN,         // Réception (Achat, Retour client)
        OUT,        // Sortie (Vente, Perte, Casse)
        TRANSFER,   // Déplacement inter-agences
        ADJUSTMENT  // Régularisation d'inventaire
    }

    public enum MovementStatus {
        DRAFT,      // Brouillon (pas d'impact stock)
        VALIDATED,  // Confirmé (impact stock)
        CANCELLED   // Annulé
    }
}