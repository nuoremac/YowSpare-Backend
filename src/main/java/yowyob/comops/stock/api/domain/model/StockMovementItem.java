package yowyob.comops.stock.api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementItem {
    private UUID id;
    private UUID productId;
    
    // Champs enrichis (non persistés ici, mais utiles pour l'affichage)
    private String productName; 
    private String productCode;
    
    private Integer quantity;
    private BigDecimal costPrice; // Prix de revient unitaire au moment du mouvement
}