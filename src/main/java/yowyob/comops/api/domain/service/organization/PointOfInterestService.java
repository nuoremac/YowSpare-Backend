package yowyob.comops.api.domain.service.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.PointOfInterest;
import yowyob.comops.api.domain.port.in.organization.PointOfInterestUseCase;
import yowyob.comops.api.domain.port.out.organization.PointOfInterestRepositoryPort;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PointOfInterestService implements PointOfInterestUseCase {
    private final PointOfInterestRepositoryPort repository;

    @Override
    public Mono<PointOfInterest> createPoi(PointOfInterest poi) {
        return repository.save(poi);
    }

    @Override
    public Flux<PointOfInterest> getAllPois() {
        return repository.findAll();
    }

    @Override
    public Mono<Void> linkAgencyToPoi(UUID agencyId, UUID poiId, Integer distanceMeters, String description) {
        return repository.addAgencyLink(agencyId, poiId, distanceMeters, description);
    }

    @Override
    public Mono<Void> unlinkAgencyFromPoi(UUID agencyId, UUID poiId) {
        return repository.removeAgencyLink(agencyId, poiId);
    }
}