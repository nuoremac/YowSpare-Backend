package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.ProductCategory;
import yowyob.comops.stock.api.domain.port.out.CategoryRepositoryPort;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.mapper.CategoryMapper;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcCategoryRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {
    private final R2dbcCategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public Mono<ProductCategory> save(ProductCategory category) {
        CategoryEntity entity = mapper.toEntity(category);
        if (entity.getId() == null)
            entity.setId(null);
        return repository.save(entity).map(mapper::toDomain);
    }

    @Override
    public Flux<ProductCategory> findAllByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId).map(mapper::toDomain);
    }

    @Override
    public Mono<ProductCategory> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}