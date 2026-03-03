package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.AgencyPoiEntity;
import java.util.UUID;

@Repository
public interface R2dbcAgencyPoiRepository extends ReactiveCrudRepository<AgencyPoiEntity, UUID> {
    @Modifying
    @Query("DELETE FROM agency_pois WHERE agency_id = :agencyId AND poi_id = :poiId")
    Mono<Void> deleteByAgencyIdAndPoiId(UUID agencyId, UUID poiId);
}