package yowyob.comops.api.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.*;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import yowyob.comops.api.domain.port.out.thirdparty.SupplierRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.thirdparty.SupplierDetailsEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.thirdparty.ThirdPartyEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.thirdparty.R2dbcSupplierDetailsRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.thirdparty.R2dbcThirdPartyRepository;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SupplierRepositoryAdapter implements SupplierRepositoryPort {
    private final R2dbcThirdPartyRepository thirdPartyRepository;
    private final R2dbcSupplierDetailsRepository supplierDetailsRepository;

    @Override
    @Transactional
    public Mono<Supplier> save(Supplier supplier) {
        ThirdPartyEntity entity = mapToEntity(supplier);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
            entity.setCreatedAt(Instant.now());
        }
        entity.setUpdatedAt(Instant.now());

        return thirdPartyRepository.save(entity)
                .flatMap(savedBase -> {
                    SupplierDetailsEntity details = mapToDetails(supplier, savedBase.getId());
                    return supplierDetailsRepository.save(details)
                            .map(savedDetails -> mapToDomain(savedBase, savedDetails));
                });
    }

    @Override
    public Mono<Supplier> findById(UUID id) {
        return Mono.zip(
                thirdPartyRepository.findById(id),
                supplierDetailsRepository.findById(id)
        ).map(tuple -> mapToDomain(tuple.getT1(), tuple.getT2()));
    }

    @Override
    public Flux<Supplier> findAllByTenantId(UUID tenantId) {
        return thirdPartyRepository.findByTenantId(tenantId)
                .flatMap(base -> supplierDetailsRepository.findById(base.getId())
                        .map(details -> mapToDomain(base, details)));
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(UUID id) {
        return supplierDetailsRepository.deleteById(id)
                .then(thirdPartyRepository.deleteById(id));
    }

    @Override
    public Mono<Boolean> existsByCode(String code, UUID tenantId) {
        return Flux.from(thirdPartyRepository.findByTenantId(tenantId))
                .filter(e -> e.getCode().equals(code))
                .hasElements();
    }

    @Override
    public Mono<Supplier> findByBankAccountNumber(String bankAccountNumber) {
        return thirdPartyRepository.findByBankAccountNumber(bankAccountNumber)
                .flatMap(base -> supplierDetailsRepository.findById(base.getId())
                        .map(details -> mapToDomain(base, details)));
    }

    @Override
    public Mono<Supplier> findByAccountingAccount(String accountingAccount) {
        return thirdPartyRepository.findByAccountingAccount(accountingAccount)
                .flatMap(base -> supplierDetailsRepository.findById(base.getId())
                        .map(details -> mapToDomain(base, details)));
    }

    @Override
    public Mono<ThirdPartyStatistics> getStatistics(UUID tenantId) {
        return Mono.zip(
                thirdPartyRepository.countSuppliersByTenantId(tenantId),
                thirdPartyRepository.countActiveSuppliersByTenantId(tenantId),
                thirdPartyRepository.countInactiveSuppliersByTenantId(tenantId)
        ).map(tuple -> ThirdPartyStatistics.builder()
                .total(tuple.getT1())
                .active(tuple.getT2())
                .inactive(tuple.getT3())
                .build());
    }

    // MAPPERS
    private ThirdPartyEntity mapToEntity(Supplier domain) {
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
        entity.setType(domain.getType() != null ? domain.getType().name() : ThirdPartyType.COMPANY.name()); // Default or mapped
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

    private SupplierDetailsEntity mapToDetails(Supplier domain, UUID id) {
        SupplierDetailsEntity details = new SupplierDetailsEntity();
        details.setId(id);
        details.setPaymentMode(domain.getPaymentMode() != null ? domain.getPaymentMode().name() : null);
        details.setMainProductType(domain.getMainProductType() != null ? domain.getMainProductType().name() : null);
        details.setDeliveryLeadTime(domain.getDeliveryLeadTime());
        details.setCertification(domain.getCertification());
        return details;
    }

    private Supplier mapToDomain(ThirdPartyEntity base, SupplierDetailsEntity details) {
        return Supplier.builder()
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
                .paymentMode(safeEnum(PaymentMode.class, details.getPaymentMode()))
                .mainProductType(safeEnum(MainProductType.class, details.getMainProductType()))
                .deliveryLeadTime(details.getDeliveryLeadTime())
                .certification(details.getCertification())
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
