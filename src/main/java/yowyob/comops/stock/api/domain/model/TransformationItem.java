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
public class TransformationItem {
    private UUID id;
    private UUID productId;
    private String productName; // Enrichi
    private TransformationItemType type; // INPUT ou OUTPUT
    private Integer quantity;

    public enum TransformationItemType {
        INPUT, // Consommé (Matière première)
        OUTPUT // Produit (Produit fini)
    }
}