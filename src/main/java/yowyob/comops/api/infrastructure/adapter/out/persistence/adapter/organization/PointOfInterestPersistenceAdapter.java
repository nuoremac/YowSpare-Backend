package yowyob.comops.api.infrastructure.adapter.out.persistence.adapter.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.PointOfInterest;
import yowyob.comops.api.domain.port.out.organization.PointOfInterestRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.AgencyPoiEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.mapper.organization.PointOfInterestMapper;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization.R2dbcAgencyPoiRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization.R2dbcPointOfInterestRepository;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PointOfInterestPersistenceAdapter implements PointOfInterestRepositoryPort {
    private final R2dbcPointOfInterestRepository repository;
    private final R2dbcAgencyPoiRepository linkRepository;
    private final PointOfInterestMapper mapper;

    @Override
    public Mono<PointOfInterest> save(PointOfInterest poi) {
        return repository.save(mapper.toEntity(poi)).map(mapper::toDomain);
    }

    @Override
    public Flux<PointOfInterest> findAll() {
        return repository.findAll().map(mapper::toDomain);
    }

    @Override
    public Mono<PointOfInterest> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Mono<Void> addAgencyLink(UUID agencyId, UUID poiId, Integer distance, String description) {
        return linkRepository.save(new AgencyPoiEntity(agencyId, poiId, distance, description)).then();
    }

    @Override
    public Mono<Void> removeAgencyLink(UUID agencyId, UUID poiId) {
        return linkRepository.deleteByAgencyIdAndPoiId(agencyId, poiId);
    }
}