package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import yowyob.comops.stock.api.domain.model.InventoryCount;
import yowyob.comops.stock.api.domain.model.InventorySession;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.InventoryCountEntity;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.InventorySessionEntity;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    @Mapping(target = "counts", ignore = true)
    InventorySession toDomain(InventorySessionEntity entity);

    InventorySessionEntity toEntity(InventorySession domain);

    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "productSku", ignore = true)
    InventoryCount toDomainCount(InventoryCountEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sessionId", ignore = true)
    InventoryCountEntity toEntityCount(InventoryCount domain);
}