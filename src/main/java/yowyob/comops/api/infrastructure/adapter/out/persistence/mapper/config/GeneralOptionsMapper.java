package yowyob.comops.api.infrastructure.adapter.out.persistence.mapper.config;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import yowyob.comops.api.domain.model.config.AgencySettings;
import yowyob.comops.api.domain.model.config.AppBusinessSettings;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.config.AgencySettingsEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.config.AppBusinessSettingsEntity;

@Mapper(componentModel = "spring")
public interface GeneralOptionsMapper {
    @Mapping(target = "id", ignore = true) // L'ID du domain model n'est pas utilisé ici
    AppBusinessSettings toDomainGlobal(AppBusinessSettingsEntity entity);
    
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AppBusinessSettingsEntity toEntityGlobal(AppBusinessSettings domain);

    @Mapping(target = "id", ignore = true)
    AgencySettings toDomainLocal(AgencySettingsEntity entity);

    @Mapping(target = "updatedAt", ignore = true)
    AgencySettingsEntity toEntityLocal(AgencySettings domain);
}