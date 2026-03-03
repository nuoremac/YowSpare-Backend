package yowyob.comops.api.domain.port.out.organization;

import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.BusinessActor;
import java.util.UUID;

public interface BusinessActorRepositoryPort {
    Mono<BusinessActor> save(BusinessActor actor);

    Mono<BusinessActor> findById(UUID id);
}