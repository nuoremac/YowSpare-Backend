package yowyob.comops.stock.api.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import yowyob.comops.stock.api.domain.model.StockLevel;
import yowyob.comops.stock.api.domain.port.in.StockLevelUseCase;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.StockLevelEntity;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcProductRepository;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository.R2dbcStockLevelRepository;
import yowyob.comops.stock.api.infrastructure.config.security.SecurityUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockLevelService implements StockLevelUseCase {
    private final R2dbcStockLevelRepository stockRepository;
    private final R2dbcProductRepository productRepository; // Pour enrichir les noms
    private final SecurityUtils securityUtils;

    @Override
    public Flux<StockLevel> getStockLevels(UUID organizationId) {
        return securityUtils.getUserContext().flatMapMany(context -> {
            Flux<StockLevelEntity> flux;

            // 1. Filtrage strict par Agence
            if (context.getAgencyId() == null) {
                // Global : voit tout le stock de l'org
                flux = stockRepository.findByOrganizationId(organizationId);
            } else {
                // Local : ne voit que son stock
                flux = stockRepository.findByOrganizationIdAndAgencyId(organizationId, context.getAgencyId());
            }

            return flux.flatMap(this::mapToDomain);
        });
    }

    private reactor.core.publisher.Mono<StockLevel> mapToDomain(StockLevelEntity entity) {
        return productRepository.findById(entity.getProductId())
                .map(product -> StockLevel.builder()
                        .id(entity.getId())
                        .productId(entity.getProductId())
                        .productName(product.getName())
                        .productSku(product.getSku())
                        .agencyId(entity.getAgencyId())
                        .quantity(entity.getQuantity())
                        .availableQuantity(entity.getQuantity() - (entity.getReservedQuantity() != null ? entity.getReservedQuantity() : 0))
                        .lastUpdated(entity.getLastUpdated())
                        .build());
    }
}