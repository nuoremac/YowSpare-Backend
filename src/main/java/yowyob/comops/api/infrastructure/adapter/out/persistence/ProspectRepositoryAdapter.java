package yowyob.comops.api.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.*;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import yowyob.comops.api.domain.port.out.thirdparty.ProspectRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.thirdparty.ProspectDetailsEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.thirdparty.ThirdPartyEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.thirdparty.R2dbcProspectDetailsRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.thirdparty.R2dbcThirdPartyRepository;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProspectRepositoryAdapter implements ProspectRepositoryPort {
    private final R2dbcThirdPartyRepository thirdPartyRepository;
    private final R2dbcProspectDetailsRepository prospectDetailsRepository;

    @Override
    @Transactional
    public Mono<Prospect> save(Prospect prospect) {
        ThirdPartyEntity entity = mapToEntity(prospect);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
            entity.setCreatedAt(Instant.now());
        }
        entity.setUpdatedAt(Instant.now());

        return thirdPartyRepository.save(entity)
                .flatMap(savedBase -> {
                    ProspectDetailsEntity details = mapToDetails(prospect, savedBase.getId());
                    return prospectDetailsRepository.save(details)
                            .map(savedDetails -> mapToDomain(savedBase, savedDetails));
                });
    }

    @Override
    public Mono<Prospect> findById(UUID id) {
        return Mono.zip(
                thirdPartyRepository.findById(id),
                prospectDetailsRepository.findById(id)
        ).map(tuple -> mapToDomain(tuple.getT1(), tuple.getT2()));
    }

    @Override
    public Flux<Prospect> findAllByTenantId(UUID tenantId) {
        return thirdPartyRepository.findByTenantId(tenantId)
                .flatMap(base -> prospectDetailsRepository.findById(base.getId())
                        .map(details -> mapToDomain(base, details)));
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(UUID id) {
        return prospectDetailsRepository.deleteById(id)
                .then(thirdPartyRepository.deleteById(id));
    }

    @Override
    public Mono<Boolean> existsByCode(String code, UUID tenantId) {
        return Flux.from(thirdPartyRepository.findByTenantId(tenantId))
                .filter(e -> e.getCode().equals(code))
                .hasElements();
    }

    @Override
    public Mono<Prospect> findByBankAccountNumber(String bankAccountNumber) {
        return thirdPartyRepository.findByBankAccountNumber(bankAccountNumber)
                .flatMap(base -> prospectDetailsRepository.findById(base.getId())
                        .map(details -> mapToDomain(base, details)));
    }

    @Override
    public Mono<Prospect> findByAccountingAccount(String accountingAccount) {
        return thirdPartyRepository.findByAccountingAccount(accountingAccount)
                .flatMap(base -> prospectDetailsRepository.findById(base.getId())
                        .map(details -> mapToDomain(base, details)));
    }

    @Override
    public Mono<ThirdPartyStatistics> getStatistics(UUID tenantId) {
        return Mono.zip(
                thirdPartyRepository.countProspectsByTenantId(tenantId),
                thirdPartyRepository.countActiveProspectsByTenantId(tenantId),
                thirdPartyRepository.countInactiveProspectsByTenantId(tenantId)
        ).map(tuple -> ThirdPartyStatistics.builder()
                .total(tuple.getT1())
                .active(tuple.getT2())
                .inactive(tuple.getT3())
                .build());
    }

    @Override
    public Mono<Long> countConvertedProspects(UUID tenantId) {
        return thirdPartyRepository.countConvertedProspectsByTenantId(tenantId);
    }

    // MAPPERS
    private ThirdPartyEntity mapToEntity(Prospect domain) {
        ThirdPartyEntity entity = new ThirdPartyEntity();
        entity.setId(domain.getId());
        entity.setTenantId(domain.getTenantId());
        entity.setAgencyId(domain.getAgencyId());
        entity.setCode(domain.getCode());
        entity.setName(domain.getName());
        entity.setShortName(domain.getShortName());
        entity.setDescription(domain.getDescription());
        entity.setAccountingAccount(domain.getAccountingAccount());
        entity.setBankAccountNumber(domain.getBankAccountNumber());
        entity.setTaxNumber(domain.getTaxNumber());
        entity.setTradeRegistryNumber(domain.getTradeRegistryNumber());
        entity.setType(domain.getType() != null ? domain.getType().name() : ThirdPartyType.COMPANY.name());
        entity.setBusinessSector(domain.getBusinessSector() != null ? domain.getBusinessSector().name() : null);
        entity.setCompanySize(domain.getCompanySize() != null ? domain.getCompanySize().name() : null);
        entity.setEmail(domain.getEmail());
        entity.setPhoneNumber(domain.getPhoneNumber());
        entity.setWebsite(domain.getWebsite());
        entity.setPreferredChannel(domain.getPreferredChannel() != null ? domain.getPreferredChannel().name() : null);
        entity.setAddress(domain.getAddress());
        entity.setComplement(domain.getAddressComplement());
        entity.setPostalCode(domain.getPostalCode());
        entity.setCity(domain.getCity());
        entity.setCountry(domain.getCountry());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private ProspectDetailsEntity mapToDetails(Prospect domain, UUID id) {
        ProspectDetailsEntity details = new ProspectDetailsEntity();
        details.setId(id);
        details.setSource(domain.getSource() != null ? domain.getSource().name() : null);
        details.setPotential(domain.getPotential() != null ? domain.getPotential().name() : null);
        details.setProbability(domain.getProbability());
        details.setConversionDate(domain.getConversionDate());
        details.setNotes(domain.getNotes());
        return details;
    }

    private Prospect mapToDomain(ThirdPartyEntity base, ProspectDetailsEntity details) {
        return Prospect.builder()
                .id(base.getId())
                .tenantId(base.getTenantId())
                .agencyId(base.getAgencyId())
                .code(base.getCode())
                .name(base.getName())
                .shortName(base.getShortName())
                .description(base.getDescription())
                .accountingAccount(base.getAccountingAccount())
                .bankAccountNumber(base.getBankAccountNumber())
                .taxNumber(base.getTaxNumber())
                .tradeRegistryNumber(base.getTradeRegistryNumber())
                .type(safeEnum(ThirdPartyType.class, base.getType()))
                .businessSector(safeEnum(BusinessSector.class, base.getBusinessSector()))
                .companySize(safeEnum(CompanySize.class, base.getCompanySize()))
                .email(base.getEmail())
                .phoneNumber(base.getPhoneNumber())
                .website(base.getWebsite())
                .preferredChannel(safeEnum(PreferredChannel.class, base.getPreferredChannel()))
                .address(base.getAddress())
                .addressComplement(base.getComplement())
                .postalCode(base.getPostalCode())
                .city(base.getCity())
                .country(base.getCountry())
                .active(base.getActive() != null ? base.getActive() : false)
                .createdAt(base.getCreatedAt())
                .updatedAt(base.getUpdatedAt())
                // Details
                .source(safeEnum(ProspectSource.class, details.getSource()))
                .potential(safeEnum(ProspectPotential.class, details.getPotential()))
                .probability(details.getProbability())
                .conversionDate(details.getConversionDate())
                .notes(details.getNotes())
                .build();
    }

    private <T extends Enum<T>> T safeEnum(Class<T> enumClass, String value) {
        if (value == null) return null;
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
