package yowyob.comops.api.domain.model.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointOfInterest {
    private UUID id;
    private String name;
    private String type; // HOTEL, STATION, etc.
    private String description;
    private String mediaUri;
    private Double latitude;
    private Double longitude;

    // Attribut contextuel (quand lié à une agence)
    private Integer distanceMeters;
    private String relationDescription;
}