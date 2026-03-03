package yowyob.comops.api.domain.port.in.organization;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.Organization;
import java.util.UUID;

public interface OrganizationUseCase {
    Mono<Organization> createOrganization(Organization organization, UUID ownerActorId);

    Mono<Organization> updateOrganization(UUID organizationId, Organization details, UUID requesterActorId);

    Mono<Organization> getOrganizationById(UUID organizationId);

    Flux<Organization> getMyOrganizations(UUID actorId);

    Mono<Organization> transferOwnership(UUID organizationId, UUID currentOwnerId, UUID newOwnerId);
}
