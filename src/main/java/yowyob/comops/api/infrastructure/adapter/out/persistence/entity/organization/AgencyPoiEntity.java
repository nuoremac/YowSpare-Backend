package yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("agency_pois")
public class AgencyPoiEntity {
    @Column("agency_id")
    private UUID agencyId;
    @Column("poi_id")
    private UUID poiId;
    @Column("distance_meters")
    private Integer distanceMeters;
    private String description;
}