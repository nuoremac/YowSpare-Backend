package yowyob.comops.api.infrastructure.adapter.out.persistence.mapper.organization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yowyob.comops.api.domain.model.organization.Agency;
import yowyob.comops.api.domain.model.organization.PointOfInterest;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.AgencyEntity;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AgencyMapper {
    private final ObjectMapper objectMapper;

    public Agency toDomain(AgencyEntity entity, List<PointOfInterest> pois) {
        if (entity == null)
            return null;

        Agency.AgencyBuilder builder = Agency.builder()
                .id(entity.getId())
                .organizationId(entity.getOrganizationId())
                .code(entity.getCode())
                .name(entity.getName())
                .shortName(entity.getShortName())
                .longName(entity.getLongName())
                .type(entity.getType())
                .location(entity.getLocation())
                .address(entity.getAddress())
                .city(entity.getCity())
                .country(entity.getCountry())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .timezone(entity.getTimezone())
                .ownerId(entity.getOwnerId())
                .managerId(entity.getManagerId())
                .transferable(entity.getTransferable())
                .isHeadquarter(entity.isHeadquarter())
                .isActive(entity.isActive())
                .isPublic(entity.getIsPublic())
                .isBusiness(entity.getIsBusiness())
                .isIndividualBusiness(entity.getIsIndividualBusiness())
                .logoUri(entity.getLogoUri())
                .logoId(entity.getLogoId())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .whatsapp(entity.getWhatsapp())
                .socialNetwork(entity.getSocialNetwork())
                .greetingMessage(entity.getGreetingMessage())
                .description(entity.getDescription())
                .openTime(entity.getOpenTime())
                .closeTime(entity.getCloseTime())
                .averageRevenue(entity.getAverageRevenue())
                .capitalShare(entity.getCapitalShare())
                .registrationNumber(entity.getRegistrationNumber())
                .taxNumber(entity.getTaxNumber())
                .totalAffiliatedCustomers(entity.getTotalAffiliatedCustomers())
                .nearbyPoints(pois != null ? pois : Collections.emptyList())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        // Conversion JSON -> List<String> pour Keywords
        if (entity.getKeywords() != null && !entity.getKeywords().isEmpty()) {
            try {
                builder.keywords(objectMapper.readValue(entity.getKeywords(), new TypeReference<List<String>>() {
                }));
            } catch (JsonProcessingException e) {
                builder.keywords(Collections.emptyList());
            }
        } else {
            builder.keywords(Collections.emptyList());
        }

        return builder.build();
    }

    // Surcharge pour usage sans POIs
    public Agency toDomain(AgencyEntity entity) {
        return toDomain(entity, Collections.emptyList());
    }

    public AgencyEntity toEntity(Agency domain) {
        if (domain == null)
            return null;

        AgencyEntity entity = new AgencyEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setCode(domain.getCode());
        entity.setName(domain.getName());
        entity.setShortName(domain.getShortName());
        entity.setLongName(domain.getLongName());
        entity.setType(domain.getType());
        entity.setLocation(domain.getLocation());
        entity.setAddress(domain.getAddress());
        entity.setCity(domain.getCity());
        entity.setCountry(domain.getCountry());
        entity.setLatitude(domain.getLatitude());
        entity.setLongitude(domain.getLongitude());
        entity.setTimezone(domain.getTimezone());
        entity.setOwnerId(domain.getOwnerId());
        entity.setManagerId(domain.getManagerId());
        entity.setTransferable(domain.getTransferable());

        // Handling Boolean/boolean types carefully
        entity.setHeadquarter(Boolean.TRUE.equals(domain.getIsHeadquarter()));
        entity.setActive(Boolean.TRUE.equals(domain.getIsActive()));
        entity.setIsPublic(domain.getIsPublic());
        entity.setIsBusiness(domain.getIsBusiness());
        entity.setIsIndividualBusiness(domain.getIsIndividualBusiness());

        entity.setLogoUri(domain.getLogoUri());
        entity.setLogoId(domain.getLogoId());
        entity.setPhone(domain.getPhone());
        entity.setEmail(domain.getEmail());
        entity.setWhatsapp(domain.getWhatsapp());
        entity.setSocialNetwork(domain.getSocialNetwork());
        entity.setGreetingMessage(domain.getGreetingMessage());
        entity.setDescription(domain.getDescription());
        entity.setOpenTime(domain.getOpenTime());
        entity.setCloseTime(domain.getCloseTime());
        entity.setAverageRevenue(domain.getAverageRevenue());
        entity.setCapitalShare(domain.getCapitalShare());
        entity.setRegistrationNumber(domain.getRegistrationNumber());
        entity.setTaxNumber(domain.getTaxNumber());
        entity.setTotalAffiliatedCustomers(domain.getTotalAffiliatedCustomers());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        // Conversion List<String> -> JSON
        if (domain.getKeywords() != null) {
            try {
                entity.setKeywords(objectMapper.writeValueAsString(domain.getKeywords()));
            } catch (JsonProcessingException e) {
                entity.setKeywords("[]");
            }
        }

        return entity;
    }
}
