package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.PointOfInterestEntity;
import java.util.UUID;

@Repository
public interface R2dbcPointOfInterestRepository extends ReactiveCrudRepository<PointOfInterestEntity, UUID> {
    @Query("SELECT p.* FROM point_of_interests p JOIN agency_pois ap ON p.id = ap.poi_id WHERE ap.agency_id = :agencyId")
    Flux<PointOfInterestEntity> findByAgencyId(UUID agencyId);
}
