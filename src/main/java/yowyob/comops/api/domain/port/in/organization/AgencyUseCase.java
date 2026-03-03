package yowyob.comops.api.domain.port.in.organization;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.Agency;
import java.util.UUID;

public interface AgencyUseCase {
    Flux<Agency> getAllWarehouses(UUID organizationId);

    Flux<Agency> getAllAgencies(UUID organizationId);

    Mono<Agency> createAgency(Agency agency);

    Mono<Agency> updateAgency(UUID id, Agency agency);

    Mono<Void> deleteAgency(UUID id);
}