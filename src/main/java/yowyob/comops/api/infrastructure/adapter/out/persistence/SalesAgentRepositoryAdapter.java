package yowyob.comops.api.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.*;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import yowyob.comops.api.domain.port.out.thirdparty.SalesAgentRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.thirdparty.SalesAgentDetailsEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.thirdparty.ThirdPartyEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.thirdparty.R2dbcSalesAgentDetailsRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.thirdparty.R2dbcThirdPartyRepository;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SalesAgentRepositoryAdapter implements SalesAgentRepositoryPort {
    private final R2dbcThirdPartyRepository thirdPartyRepository;
    private final R2dbcSalesAgentDetailsRepository salesAgentDetailsRepository;

    @Override
    @Transactional
    public Mono<SalesAgent> save(SalesAgent agent) {
        ThirdPartyEntity entity = mapToEntity(agent);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
            entity.setCreatedAt(Instant.now());
        }
        entity.setUpdatedAt(Instant.now());

        return thirdPartyRepository.save(entity)
                .flatMap(savedBase -> {
                    SalesAgentDetailsEntity details = mapToDetails(agent, savedBase.getId());
                    return salesAgentDetailsRepository.save(details)
                            .map(savedDetails -> mapToDomain(savedBase, savedDetails));
                });
    }

    @Override
    public Mono<SalesAgent> findById(UUID id) {
        return Mono.zip(
                thirdPartyRepository.findById(id),
                salesAgentDetailsRepository.findById(id)
        ).map(tuple -> mapToDomain(tuple.getT1(), tuple.getT2()));
    }

    @Override
    public Flux<SalesAgent> findAllByTenantId(UUID tenantId) {
        return thirdPartyRepository.findByTenantId(tenantId)
                .flatMap(base -> salesAgentDetailsRepository.findById(base.getId())
                        .map(details -> mapToDomain(base, details)));
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(UUID id) {
        return salesAgentDetailsRepository.deleteById(id)
                .then(thirdPartyRepository.deleteById(id));
    }

    @Override
    public Mono<Boolean> existsByCode(String code, UUID tenantId) {
        return Flux.from(thirdPartyRepository.findByTenantId(tenantId))
                .filter(e -> e.getCode().equals(code))
                .hasElements();
    }

    @Override
    public Mono<SalesAgent> findByBankAccountNumber(String bankAccountNumber) {
        return thirdPartyRepository.findByBankAccountNumber(bankAccountNumber)
                .flatMap(base -> salesAgentDetailsRepository.findById(base.getId())
                        .map(details -> mapToDomain(base, details)));
    }

    @Override
    public Mono<SalesAgent> findByAccountingAccount(String accountingAccount) {
        return thirdPartyRepository.findByAccountingAccount(accountingAccount)
                .flatMap(base -> salesAgentDetailsRepository.findById(base.getId())
                        .map(details -> mapToDomain(base, details)));
    }

    @Override
    public Mono<ThirdPartyStatistics> getStatistics(UUID tenantId) {
        return Mono.zip(
                thirdPartyRepository.countSalesAgentsByTenantId(tenantId),
                thirdPartyRepository.countActiveSalesAgentsByTenantId(tenantId),
                thirdPartyRepository.countInactiveSalesAgentsByTenantId(tenantId)
        ).map(tuple -> ThirdPartyStatistics.builder()
                .total(tuple.getT1())
                .active(tuple.getT2())
                .inactive(tuple.getT3())
                .build());
    }

    // MAPPERS
    private ThirdPartyEntity mapToEntity(SalesAgent domain) {
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

    private SalesAgentDetailsEntity mapToDetails(SalesAgent domain, UUID id) {
        SalesAgentDetailsEntity details = new SalesAgentDetailsEntity();
        details.setId(id);
        details.setAgentType(domain.getAgentType() != null ? domain.getAgentType().name() : null);
        details.setCoveredZones(domain.getCoveredZones());
        details.setSpecializations(domain.getSpecializations());
        details.setCommission(domain.getCommission());
        details.setContractStartDate(domain.getContractStartDate());
        details.setContractEndDate(domain.getContractEndDate());
        return details;
    }

    private SalesAgent mapToDomain(ThirdPartyEntity base, SalesAgentDetailsEntity details) {
        return SalesAgent.builder()
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
                .agentType(safeEnum(SalesAgentType.class, details.getAgentType()))
                .coveredZones(details.getCoveredZones())
                .specializations(details.getSpecializations())
                .commission(details.getCommission())
                .contractStartDate(details.getContractStartDate())
                .contractEndDate(details.getContractEndDate())
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
