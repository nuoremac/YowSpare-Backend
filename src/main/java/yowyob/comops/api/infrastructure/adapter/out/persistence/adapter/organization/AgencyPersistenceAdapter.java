package yowyob.comops.api.infrastructure.adapter.out.persistence.adapter.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.Agency;
import yowyob.comops.api.domain.port.out.organization.AgencyRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.mapper.organization.AgencyMapper;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization.R2dbcAgencyRepository;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgencyPersistenceAdapter implements AgencyRepositoryPort {
    private final R2dbcAgencyRepository repository;
    private final AgencyMapper mapper;

    @Override
    public Mono<Agency> save(Agency agency) {
        if (agency.getCreatedAt() == null)
            agency.setCreatedAt(Instant.now());
        return repository.save(mapper.toEntity(agency)).map(mapper::toDomain);
    }

    @Override
    public Mono<Agency> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Flux<Agency> findAllByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId).map(mapper::toDomain);
    }

    @Override
    public Flux<Agency> findAllWarehouses(UUID organizationId) {
        return repository.findByOrganizationIdAndType(organizationId, "WAREHOUSE").map(mapper::toDomain);
    }

    @Override
    public Mono<Agency> findHeadquarter(UUID organizationId) {
        return repository.findByOrganizationIdAndIsHeadquarterTrue(organizationId).map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }
}