package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.Product;
import yowyob.comops.stock.api.domain.port.out.ProductRepositoryPort;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.ProductEntity;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.mapper.ProductMapper;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcCategoryRepository;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcProductRepository;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepositoryPort {
    private final R2dbcProductRepository repository;
    private final R2dbcCategoryRepository categoryRepository;
    private final ProductMapper mapper;

    @Override
    public Mono<Product> save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        if (entity.getId() == null) entity.setId(null); 
        if (entity.getCreatedAt() == null) entity.setCreatedAt(Instant.now());
        
        return repository.save(entity).flatMap(this::enrichProduct);
    }

    @Override
    public Mono<Product> findById(UUID id) {
        return repository.findById(id).flatMap(this::enrichProduct);
    }

    @Override
    public Flux<Product> findAllByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId)
                .flatMap(this::enrichProduct);
    }

    @Override
    public Flux<Product> findAllByOrganizationIdAndAgencyId(UUID organizationId, UUID agencyId) {
        return repository.findByOrganizationIdAndAgencyId(organizationId, agencyId)
                .flatMap(this::enrichProduct);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsBySkuAndOrganizationId(String sku, UUID organizationId) {
        return repository.existsBySkuAndOrganizationId(sku, organizationId);
    }

    private Mono<Product> enrichProduct(ProductEntity entity) {
        Product product = mapper.toDomain(entity);
        if (entity.getCategoryId() != null) {
            return categoryRepository.findById(entity.getCategoryId())
                    .map(CategoryEntity::getName)
                    .defaultIfEmpty("Unknown")
                    .map(name -> {
                        product.setCategoryName(name);
                        return product;
                    });
        }
        return Mono.just(product);
    }
}