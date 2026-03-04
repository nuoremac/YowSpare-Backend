package yowyob.comops.api.domain.port.out.organization;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.Organization;
import java.util.UUID;

public interface OrganizationRepositoryPort {
    Mono<Organization> save(Organization organization);

    Mono<Organization> findById(UUID id);

    Flux<Organization> findByBusinessActorId(UUID businessActorId);
}
