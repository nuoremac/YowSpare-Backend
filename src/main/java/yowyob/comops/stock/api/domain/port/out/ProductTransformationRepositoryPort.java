package yowyob.comops.stock.api.domain.port.out;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.ProductTransformation;

import java.util.UUID;

public interface ProductTransformationRepositoryPort {
    Flux<ProductTransformation> findAllByOrganizationId(UUID organizationId);

    Flux<ProductTransformation> findAllByOrganizationIdAndAgencyId(UUID organizationId, UUID agencyId);

    Mono<ProductTransformation> findById(UUID id);

    Mono<ProductTransformation> save(ProductTransformation transformation);
}