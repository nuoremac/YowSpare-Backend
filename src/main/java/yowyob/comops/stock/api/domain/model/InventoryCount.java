package yowyob.comops.stock.api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCount {
    private UUID id;
    private UUID productId;
    
    // Enrichissement
    private String productName;
    private String productSku;

    // Snapshot au moment de la création de la session
    private Integer theoreticalQuantity; 
    
    // Ce que le magasinier a compté
    private Integer physicalQuantity;
    
    // Calculé : Physical - Theoretical
    private Integer variance; 
}