package yowyob.comops.api.infrastructure.adapter.out.persistence.mapper.organization;

import org.springframework.stereotype.Component;
import yowyob.comops.api.domain.model.organization.BusinessActor;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.BusinessActorEntity;

@Component
public class BusinessActorMapper {
    public BusinessActor toDomain(BusinessActorEntity entity) {
        if (entity == null)
            return null;
        return BusinessActor.builder()
                .id(entity.getId())
                .name(entity.getName())
                .businessId(entity.getBusinessId())
                .niu(entity.getNiu())
                .tradeRegistryNumber(entity.getTradeRegistryNumber())
                .website(entity.getWebsite())
                .contactPhone(entity.getContactPhone())
                .privateAddress(entity.getPrivateAddress())
                .businessAddress(entity.getBusinessAddress())
                .businessProfile(entity.getBusinessProfile())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public BusinessActorEntity toEntity(BusinessActor domain) {
        if (domain == null)
            return null;

        BusinessActorEntity entity = new BusinessActorEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setBusinessId(domain.getBusinessId());
        entity.setNiu(domain.getNiu());
        entity.setTradeRegistryNumber(domain.getTradeRegistryNumber());
        entity.setWebsite(domain.getWebsite());
        entity.setContactPhone(domain.getContactPhone());
        entity.setPrivateAddress(domain.getPrivateAddress());
        entity.setBusinessAddress(domain.getBusinessAddress());
        entity.setBusinessProfile(domain.getBusinessProfile());
        entity.setCreatedAt(domain.getCreatedAt());

        return entity;
    }
}
