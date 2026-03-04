package yowyob.comops.api.domain.port.in.organization;

import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.BusinessActor;
import java.util.UUID;

public interface BusinessActorUseCase {
    Mono<BusinessActor> createActorFromUser(UUID userId, BusinessActor actorDetails);

    Mono<BusinessActor> getCurrentActor(UUID userId);

    Mono<BusinessActor> updateActor(UUID actorId, BusinessActor actorDetails);
}