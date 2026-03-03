package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.StockMovement;
import yowyob.comops.stock.api.domain.model.StockMovementItem;
import yowyob.comops.stock.api.domain.port.out.StockMovementRepositoryPort;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.ProductEntity;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.StockMovementEntity;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.StockMovementItemEntity;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.mapper.StockMovementMapper;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcProductRepository;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcStockMovementItemRepository;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcStockMovementRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StockMovementPersistenceAdapter implements StockMovementRepositoryPort {
    private final R2dbcStockMovementRepository movementRepository;
    private final R2dbcStockMovementItemRepository itemRepository;
    private final R2dbcProductRepository productRepository;
    private final StockMovementMapper mapper;

    @Override
    @Transactional
    public Mono<StockMovement> save(StockMovement movement) {
        StockMovementEntity entity = mapper.toEntity(movement);
        if (entity.getId() == null) entity.setId(null);
        if (entity.getCreatedAt() == null) entity.setCreatedAt(Instant.now());

        return movementRepository.save(entity)
                .flatMap(savedEntity -> {
                    // Sauvegarde des items en cascade
                    List<StockMovementItemEntity> itemEntities = movement.getItems().stream()
                            .map(item -> {
                                StockMovementItemEntity ie = mapper.toEntityItem(item);
                                ie.setStockMovementId(savedEntity.getId());
                                return ie;
                            }).collect(Collectors.toList());

                    // On supprime d'abord les anciens items si update (cas rare pour un mouvement, mais sûr)
                    return itemRepository.deleteAll(itemRepository.findByStockMovementId(savedEntity.getId())) // Inefficace mais simple R2DBC
                            .then(itemRepository.saveAll(itemEntities).collectList())
                            .flatMap(savedItems -> this.enrichMovement(savedEntity, savedItems));
                });
    }

    @Override
    public Mono<StockMovement> findById(UUID id) {
        return movementRepository.findById(id)
                .flatMap(entity -> itemRepository.findByStockMovementId(entity.getId())
                        .collectList()
                        .flatMap(items -> this.enrichMovement(entity, items)));
    }

    @Override
    public Flux<StockMovement> findByOrganizationId(UUID organizationId) {
        return movementRepository.findByOrganizationId(organizationId)
                .flatMap(entity -> itemRepository.findByStockMovementId(entity.getId())
                        .collectList()
                        .flatMap(items -> this.enrichMovement(entity, items)));
    }

    @Override
    public Flux<StockMovement> findByOrganizationIdAndAgencyId(UUID organizationId, UUID agencyId) {
        return movementRepository.findByOrganizationIdAndAgencyId(organizationId, agencyId)
                .flatMap(entity -> itemRepository.findByStockMovementId(entity.getId())
                        .collectList()
                        .flatMap(items -> this.enrichMovement(entity, items)));
    }

    private Mono<StockMovement> enrichMovement(StockMovementEntity entity, List<StockMovementItemEntity> items) {
        StockMovement domain = mapper.toDomain(entity);
        
        return Flux.fromIterable(items)
                .flatMap(itemEntity -> {
                    StockMovementItem domainItem = mapper.toDomainItem(itemEntity);
                    return productRepository.findById(itemEntity.getProductId())
                            .map(prod -> {
                                domainItem.setProductName(prod.getName());
                                domainItem.setProductCode(prod.getSku());
                                return domainItem;
                            })
                            .defaultIfEmpty(domainItem);
                })
                .collectList()
                .map(domainItems -> {
                    domain.setItems(domainItems);
                    return domain;
                });
    }
}