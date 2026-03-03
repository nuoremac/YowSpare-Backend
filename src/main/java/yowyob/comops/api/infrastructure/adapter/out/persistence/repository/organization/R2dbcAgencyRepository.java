package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.AgencyEntity;
import java.util.UUID;

@Repository
public interface R2dbcAgencyRepository extends ReactiveCrudRepository<AgencyEntity, UUID> {
    Flux<AgencyEntity> findByOrganizationId(UUID organizationId);

    Flux<AgencyEntity> findByOrganizationIdAndType(UUID organizationId, String type);

    Mono<AgencyEntity> findByOrganizationIdAndIsHeadquarterTrue(UUID organizationId);
}