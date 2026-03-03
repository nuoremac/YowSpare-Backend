package yowyob.comops.api.domain.port.out.organization;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.PointOfInterest;
import java.util.UUID;

public interface PointOfInterestRepositoryPort {
    Mono<PointOfInterest> save(PointOfInterest poi);

    Flux<PointOfInterest> findAll();

    Mono<PointOfInterest> findById(UUID id);

    Mono<Void> addAgencyLink(UUID agencyId, UUID poiId, Integer distance, String description);

    Mono<Void> removeAgencyLink(UUID agencyId, UUID poiId);
}