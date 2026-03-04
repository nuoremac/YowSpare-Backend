package yowyob.comops.api.infrastructure.adapter.out.persistence.adapter.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.BusinessActor;
import yowyob.comops.api.domain.port.out.organization.BusinessActorRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.mapper.organization.BusinessActorMapper;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization.R2dbcBusinessActorRepository;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BusinessActorPersistenceAdapter implements BusinessActorRepositoryPort {
    private final R2dbcBusinessActorRepository repository;
    private final BusinessActorMapper mapper;

    @Override
    public Mono<BusinessActor> save(BusinessActor actor) {
        if (actor.getCreatedAt() == null)
            actor.setCreatedAt(Instant.now());
        return repository.save(mapper.toEntity(actor)).map(mapper::toDomain);
    }

    @Override
    public Mono<BusinessActor> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}