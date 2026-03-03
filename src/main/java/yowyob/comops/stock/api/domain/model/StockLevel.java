package yowyob.comops.stock.api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockLevel {
    private UUID id;
    private UUID productId;
    private String productName; // Enrichi pour affichage
    private String productSku;  // Enrichi
    private UUID agencyId;
    private Integer quantity;
    private Integer availableQuantity; // (quantity - reserved)
    private Instant lastUpdated;
}