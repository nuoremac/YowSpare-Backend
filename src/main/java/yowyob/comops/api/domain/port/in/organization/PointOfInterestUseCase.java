package yowyob.comops.api.domain.port.in.organization;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.PointOfInterest;
import java.util.UUID;

public interface PointOfInterestUseCase {
    Mono<PointOfInterest> createPoi(PointOfInterest poi);

    Flux<PointOfInterest> getAllPois();

    Mono<Void> linkAgencyToPoi(UUID agencyId, UUID poiId, Integer distanceMeters, String description);

    Mono<Void> unlinkAgencyFromPoi(UUID agencyId, UUID poiId);
}