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
public class ProductTransformation {
    private UUID id;
    private UUID organizationId;
    private UUID agencyId; // Lieu de production
    private String reference;
    private TransformationStatus status;
    private String description;
    private Instant date;

    private List<TransformationItem> inputs;
    private List<TransformationItem> outputs;

    private Instant createdAt;
    private Instant updatedAt;

    public enum TransformationStatus {
        DRAFT,
        VALIDATED,
        CANCELLED
    }
}