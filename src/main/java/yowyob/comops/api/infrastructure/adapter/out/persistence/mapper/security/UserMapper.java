package yowyob.comops.api.infrastructure.adapter.out.persistence.mapper.security;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import yowyob.comops.api.domain.model.security.User;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", source = "passwordHash")
    @Mapping(target = "roles", ignore = true)
    User toDomain(UserEntity entity);

    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "onboardingStep", defaultValue = "0")
    @Mapping(target = "createdAt", ignore = true)
    UserEntity toEntity(User domain);
}