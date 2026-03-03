package yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("point_of_interests")
public class PointOfInterestEntity {
    @Id
    private UUID id;
    private String name;
    private String type;
    private String description;
    @Column("media_uri")
    private String mediaUri;
    private Double latitude;
    private Double longitude;
    @Column("created_at")
    private Instant createdAt;
}
