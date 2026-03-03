package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import yowyob.comops.stock.api.domain.model.ProductTransformation;
import yowyob.comops.stock.api.domain.model.TransformationItem;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.ProductTransformationEntity;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.TransformationItemEntity;

@Mapper(componentModel = "spring")
public interface ProductTransformationMapper {
    @Mapping(target = "inputs", ignore = true)
    @Mapping(target = "outputs", ignore = true)
    ProductTransformation toDomain(ProductTransformationEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductTransformationEntity toEntity(ProductTransformation domain);

    @Mapping(target = "productName", ignore = true)
    TransformationItem toDomainItem(TransformationItemEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transformationId", ignore = true)
    TransformationItemEntity toEntityItem(TransformationItem domain);
}