package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.InventoryCount;
import yowyob.comops.stock.api.domain.model.InventorySession;
import yowyob.comops.stock.api.domain.port.out.InventoryRepositoryPort;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.InventoryCountEntity;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.InventorySessionEntity;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.mapper.InventoryMapper;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcInventoryCountRepository;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcInventorySessionRepository;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcProductRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InventoryPersistenceAdapter implements InventoryRepositoryPort {
    private final R2dbcInventorySessionRepository sessionRepository;
    private final R2dbcInventoryCountRepository countRepository;
    private final R2dbcProductRepository productRepository;
    private final InventoryMapper mapper;

    @Override
    @Transactional
    public Mono<InventorySession> save(InventorySession session) {
        InventorySessionEntity entity = mapper.toEntity(session);
        if(entity.getId() == null) entity.setId(null);

        return sessionRepository.save(entity)
                .flatMap(savedSession -> {
                    if (session.getCounts() == null || session.getCounts().isEmpty()) {
                        return this.enrichSession(savedSession, List.of());
                    }

                    List<InventoryCountEntity> countEntities = session.getCounts().stream()
                            .map(c -> {
                                InventoryCountEntity ce = mapper.toEntityCount(c);
                                ce.setSessionId(savedSession.getId());
                                return ce;
                            }).collect(Collectors.toList());

                    return countRepository.deleteBySessionId(savedSession.getId())
                            .then(countRepository.saveAll(countEntities).collectList())
                            .flatMap(savedCounts -> this.enrichSession(savedSession, savedCounts));
                });
    }

    @Override
    public Mono<InventorySession> findById(UUID id) {
        return sessionRepository.findById(id)
                .flatMap(entity -> countRepository.findBySessionId(entity.getId()).collectList()
                        .flatMap(counts -> this.enrichSession(entity, counts)));
    }

    @Override
    public Flux<InventorySession> findByAgencyId(UUID agencyId) {
        return sessionRepository.findByAgencyId(agencyId)
                .flatMap(entity -> countRepository.findBySessionId(entity.getId()).collectList()
                        .flatMap(counts -> this.enrichSession(entity, counts)));
    }

    private Mono<InventorySession> enrichSession(InventorySessionEntity entity, List<InventoryCountEntity> counts) {
        InventorySession domain = mapper.toDomain(entity);
        return Flux.fromIterable(counts)
                .flatMap(cEntity -> {
                    InventoryCount dCount = mapper.toDomainCount(cEntity);
                    return productRepository.findById(cEntity.getProductId())
                            .map(p -> {
                                dCount.setProductName(p.getName());
                                dCount.setProductSku(p.getSku());
                                return dCount;
                            }).defaultIfEmpty(dCount);
                })
                .collectList()
                .map(dCounts -> {
                    domain.setCounts(dCounts);
                    return domain;
                });
    }
}