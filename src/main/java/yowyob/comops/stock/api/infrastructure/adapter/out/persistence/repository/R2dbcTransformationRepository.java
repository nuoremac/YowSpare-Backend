package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.ProductTransformationEntity;

import java.util.UUID;

@Repository
public interface R2dbcTransformationRepository extends ReactiveCrudRepository<ProductTransformationEntity, UUID> {
    Flux<ProductTransformationEntity> findByOrganizationId(UUID organizationId);

    Flux<ProductTransformationEntity> findByOrganizationIdAndAgencyId(UUID organizationId, UUID agencyId);
}