package yowyob.comops.api.domain.port.out.organization;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.Agency;
import java.util.UUID;

public interface AgencyRepositoryPort {
    Mono<Agency> save(Agency agency);

    Mono<Agency> findById(UUID id);

    Flux<Agency> findAllByOrganizationId(UUID organizationId);

    Flux<Agency> findAllWarehouses(UUID organizationId);

    Mono<Void> deleteById(UUID id);

    Mono<Agency> findHeadquarter(UUID organizationId);
}