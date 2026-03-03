package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import yowyob.comops.stock.api.domain.model.Product;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.ProductEntity;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    @Mapping(target = "categoryName", ignore = true) // Le domaine a ce champ, mais on l'ignore car l'entité ne l'a pas pour le remplir automatiquement (on le fait manuellement dans l'adapter)
    Product toDomain(ProductEntity entity);
    
    // Pas besoin d'ignorer categoryName ici car il n'existe pas dans la cible (ProductEntity)
    // Mais on peut ignorer les dates si on veut laisser la BDD gérer
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductEntity toEntity(Product domain);
}