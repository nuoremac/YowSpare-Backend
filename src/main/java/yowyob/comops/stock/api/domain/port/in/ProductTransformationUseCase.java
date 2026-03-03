package yowyob.comops.stock.api.domain.port.in;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.ProductTransformation;

import java.util.UUID;

public interface ProductTransformationUseCase {
    Flux<ProductTransformation> getAll(UUID organizationId);

    Mono<ProductTransformation> create(ProductTransformation transformation);

    Mono<ProductTransformation> validate(UUID id, UUID userId);

    Mono<ProductTransformation> getById(UUID id);
}