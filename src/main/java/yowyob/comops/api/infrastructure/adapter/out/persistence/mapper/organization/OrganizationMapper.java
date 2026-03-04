package yowyob.comops.api.infrastructure.adapter.out.persistence.mapper.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yowyob.comops.api.domain.model.organization.Organization;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.OrganizationEntity;

@Component
@RequiredArgsConstructor
public class OrganizationMapper {
    // Si besoin de conversion JSON complexe, injecter ObjectMapper ici

    public Organization toDomain(OrganizationEntity entity) {
        if (entity == null)
            return null;

        return Organization.builder()
                .id(entity.getId())
                .businessActorId(entity.getBusinessActorId())
                .code(entity.getCode())
                .name(entity.getName())
                .serviceType(entity.getServiceType())
                .isIndividualBusiness(entity.isIndividualBusiness())
                .status(entity.getStatus())
                .managerId(entity.getManagerId())
                .email(entity.getEmail())
                .description(entity.getDescription())
                .logoUri(entity.getLogoUri())
                .logoId(entity.getLogoId())
                .websiteUrl(entity.getWebsiteUrl())
                .socialNetwork(entity.getSocialNetwork()) // Brut pour l'instant
                .businessRegistrationNumber(entity.getBusinessRegistrationNumber())
                .taxNumber(entity.getTaxNumber())
                .capitalShare(entity.getCapitalShare())
                .ceoName(entity.getCeoName())
                .yearFounded(entity.getYearFounded())
                .foundingDate(entity.getFoundingDate())
                .legalForm(entity.getLegalForm())
                .keywords(entity.getKeywords())
                .numberOfEmployees(entity.getNumberOfEmployees())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public OrganizationEntity toEntity(Organization domain) {
        if (domain == null)
            return null;

        OrganizationEntity entity = new OrganizationEntity();
        entity.setId(domain.getId());
        entity.setBusinessActorId(domain.getBusinessActorId());
        entity.setCode(domain.getCode());
        entity.setName(domain.getName());
        entity.setServiceType(domain.getServiceType());
        entity.setIndividualBusiness(domain.isIndividualBusiness());
        entity.setStatus(domain.getStatus());
        entity.setManagerId(domain.getManagerId());
        entity.setEmail(domain.getEmail());
        entity.setDescription(domain.getDescription());
        entity.setLogoUri(domain.getLogoUri());
        entity.setLogoId(domain.getLogoId());
        entity.setWebsiteUrl(domain.getWebsiteUrl());
        entity.setSocialNetwork(domain.getSocialNetwork());
        entity.setBusinessRegistrationNumber(domain.getBusinessRegistrationNumber());
        entity.setTaxNumber(domain.getTaxNumber());
        entity.setCapitalShare(domain.getCapitalShare());
        entity.setCeoName(domain.getCeoName());
        entity.setYearFounded(domain.getYearFounded());
        entity.setFoundingDate(domain.getFoundingDate());
        entity.setLegalForm(domain.getLegalForm());
        entity.setKeywords(domain.getKeywords());
        entity.setNumberOfEmployees(domain.getNumberOfEmployees());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }
}
