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
public class ProductCategory {
    private UUID id;
    private UUID organizationId;
    private String name;
    private String description;
    private UUID parentId; // Pour les sous-catégories
}