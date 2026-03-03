package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import yowyob.comops.stock.api.domain.model.StockMovement;
import yowyob.comops.stock.api.domain.model.StockMovementItem;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.StockMovementEntity;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.StockMovementItemEntity;

@Mapper(componentModel = "spring")
public interface StockMovementMapper {
    @Mapping(target = "items", ignore = true) // Items gérés manuellement
    StockMovement toDomain(StockMovementEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    StockMovementEntity toEntity(StockMovement domain);

    @Mapping(target = "productName", ignore = true) // Enrichissement manuel
    @Mapping(target = "productCode", ignore = true)
    StockMovementItem toDomainItem(StockMovementItemEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stockMovementId", ignore = true)
    StockMovementItemEntity toEntityItem(StockMovementItem domain);
}