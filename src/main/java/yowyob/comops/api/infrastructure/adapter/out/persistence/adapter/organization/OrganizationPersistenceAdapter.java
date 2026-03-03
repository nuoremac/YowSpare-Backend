package yowyob.comops.api.infrastructure.adapter.out.persistence.adapter.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.Organization;
import yowyob.comops.api.domain.port.out.organization.OrganizationRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.mapper.organization.OrganizationMapper;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization.R2dbcOrganizationRepository;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrganizationPersistenceAdapter implements OrganizationRepositoryPort {
    private final R2dbcOrganizationRepository repository;
    private final OrganizationMapper mapper;

    @Override
    public Mono<Organization> save(Organization organization) {
        if (organization.getCreatedAt() == null)
            organization.setCreatedAt(Instant.now());
        organization.setUpdatedAt(Instant.now());
        return repository.save(mapper.toEntity(organization)).map(mapper::toDomain);
    }

    @Override
    public Mono<Organization> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Flux<Organization> findByBusinessActorId(UUID businessActorId) {
        return repository.findByBusinessActorId(businessActorId).map(mapper::toDomain);
    }
}
