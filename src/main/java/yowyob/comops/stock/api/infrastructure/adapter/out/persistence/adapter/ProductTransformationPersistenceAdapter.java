package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.ProductTransformation;
import yowyob.comops.stock.api.domain.model.TransformationItem;
import yowyob.comops.stock.api.domain.port.out.ProductTransformationRepositoryPort;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.ProductTransformationEntity;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.TransformationItemEntity;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.mapper.ProductTransformationMapper;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcProductRepository;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcTransformationItemRepository;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcTransformationRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductTransformationPersistenceAdapter implements ProductTransformationRepositoryPort {
    private final R2dbcTransformationRepository transformationRepository;
    private final R2dbcTransformationItemRepository itemRepository;
    private final R2dbcProductRepository productRepository; // Utilisé pour l'enrichissement
    private final ProductTransformationMapper mapper;

    @Override
    public Flux<ProductTransformation> findAllByOrganizationId(UUID organizationId) {
        return transformationRepository.findByOrganizationId(organizationId)
                .flatMap(this::loadItems);
    }

    @Override
    public Flux<ProductTransformation> findAllByOrganizationIdAndAgencyId(UUID organizationId, UUID agencyId) {
        return transformationRepository.findByOrganizationIdAndAgencyId(organizationId, agencyId)
                .flatMap(this::loadItems);
    }

    @Override
    public Mono<ProductTransformation> findById(UUID id) {
        return transformationRepository.findById(id)
                .flatMap(this::loadItems);
    }

    @Override
    @Transactional
    public Mono<ProductTransformation> save(ProductTransformation domain) {
        if (domain.getCreatedAt() == null) domain.setCreatedAt(Instant.now());
        ProductTransformationEntity entity = mapper.toEntity(domain);
        if (entity.getId() == null) entity.setId(null);

        return transformationRepository.save(entity)
                .flatMap(saved -> {
                    return itemRepository.deleteByTransformationId(saved.getId())
                            .then(Mono.defer(() -> {
                                List<TransformationItemEntity> items = new ArrayList<>();
                                if (domain.getInputs() != null) {
                                    items.addAll(domain.getInputs().stream()
                                            .map(i -> createItemEntity(i, saved.getId(), "INPUT"))
                                            .toList());
                                }
                                if (domain.getOutputs() != null) {
                                    items.addAll(domain.getOutputs().stream()
                                            .map(i -> createItemEntity(i, saved.getId(), "OUTPUT"))
                                            .toList());
                                }
                                return itemRepository.saveAll(items).collectList();
                            }))
                            .flatMap(savedItems -> this.loadItems(saved));
                });
    }

    private TransformationItemEntity createItemEntity(TransformationItem item, UUID transId, String type) {
        TransformationItemEntity entity = mapper.toEntityItem(item);
        entity.setTransformationId(transId);
        entity.setType(type);
        return entity;
    }

    /**
     * Charge les items et enrichit chaque item avec le nom du produit.
     * Utilise Flux pour permettre l'asynchronisme.
     */
    private Mono<ProductTransformation> loadItems(ProductTransformationEntity entity) {
        ProductTransformation domain = mapper.toDomain(entity);

        return itemRepository.findByTransformationId(entity.getId())
                .flatMap(this::enrichItem) // Appel asynchrone pour chaque item
                .collectList()
                .map(allItems -> {
                    // Séparation en mémoire après enrichissement
                    domain.setInputs(allItems.stream()
                            .filter(i -> "INPUT".equals(i.getType().name())) // Attention: Type est maintenant un Enum dans Domain
                            .toList());
                    
                    domain.setOutputs(allItems.stream()
                            .filter(i -> "OUTPUT".equals(i.getType().name()))
                            .toList());
                    
                    return domain;
                });
    }
    
    /**
     * Convertit l'entité item en domain et va chercher le nom du produit en base.
     */
    private Mono<TransformationItem> enrichItem(TransformationItemEntity entity) {
        TransformationItem item = mapper.toDomainItem(entity);
        // On mappe la string BDD vers l'Enum Domain
        try {
            item.setType(TransformationItem.TransformationItemType.valueOf(entity.getType()));
        } catch (Exception e) {
            // Gérer le cas null ou invalide si nécessaire
        }

        return productRepository.findById(entity.getProductId())
                .map(product -> {
                    item.setProductName(product.getName());
                    return item;
                })
                .defaultIfEmpty(item); // Si produit non trouvé (supprimé?), on retourne l'item sans nom
    }
}