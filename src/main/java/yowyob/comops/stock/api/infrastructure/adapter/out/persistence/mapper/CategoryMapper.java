package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;

import yowyob.comops.stock.api.domain.model.ProductCategory;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.CategoryEntity;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    ProductCategory toDomain(CategoryEntity entity);

    CategoryEntity toEntity(ProductCategory domain);
}