package yowyob.comops.stock.api.domain.port.out;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.ProductCategory;

import java.util.UUID;

public interface CategoryRepositoryPort {
    Mono<ProductCategory> save(ProductCategory category);

    Flux<ProductCategory> findAllByOrganizationId(UUID organizationId);

    Mono<ProductCategory> findById(UUID id);
}