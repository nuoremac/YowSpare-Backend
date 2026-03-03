package yowyob.comops.api.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.*;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import yowyob.comops.api.domain.port.out.thirdparty.CustomerRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.thirdparty.CustomerDetailsEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.thirdparty.ThirdPartyEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.thirdparty.R2dbcCustomerDetailsRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.thirdparty.R2dbcThirdPartyRepository;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {
    private final R2dbcThirdPartyRepository thirdPartyRepository;
    private final R2dbcCustomerDetailsRepository customerDetailsRepository;

    @Override
    @Transactional
    public Mono<Customer> save(Customer customer) {
        ThirdPartyEntity entity = mapToEntity(customer);
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
            entity.setCreatedAt(Instant.now());
        }
        entity.setUpdatedAt(Instant.now());

        return thirdPartyRepository.save(entity)
                .flatMap(savedBase -> {
                    CustomerDetailsEntity details = mapToDetails(customer, savedBase.getId());
                    return customerDetailsRepository.save(details)
                            .map(savedDetails -> mapToDomain(savedBase, savedDetails));
                });
    }

    @Override
    public Mono<Customer> findById(UUID id) {
        return Mono.zip(
                thirdPartyRepository.findById(id),
                customerDetailsRepository.findById(id)
        ).map(tuple -> mapToDomain(tuple.getT1(), tuple.getT2()));
    }

    @Override
    public Flux<Customer> findAllByTenantId(UUID tenantId) {
        return thirdPartyRepository.findByTenantId(tenantId)
                .flatMap(base -> customerDetailsRepository.findById(base.getId())
                        .map(details -> mapToDomain(base, details)));
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(UUID id) {
        return customerDetailsRepository.deleteById(id)
                .then(thirdPartyRepository.deleteById(id));
    }

    @Override
    public Mono<Boolean> existsByCode(String code, UUID tenantId) {
        // This requires a custom query on ThirdPartyRepository, checking specifically for simplified logic here for now
        return Flux.from(thirdPartyRepository.findByTenantId(tenantId))
                .filter(e -> e.getCode().equals(code))
                .hasElements();
    }

    @Override
    public Mono<Customer> findByBankAccountNumber(String bankAccountNumber) {
        return thirdPartyRepository.findByBankAccountNumber(bankAccountNumber)
                .flatMap(base -> customerDetailsRepository.findById(base.getId())
                        .map(details -> mapToDomain(base, details)));
    }

    @Override
    public Mono<Customer> findByAccountingAccount(String accountingAccount) {
        return thirdPartyRepository.findByAccountingAccount(accountingAccount)
                .flatMap(base -> customerDetailsRepository.findById(base.getId())
                        .map(details -> mapToDomain(base, details)));
    }

    @Override
    public Mono<ThirdPartyStatistics> getStatistics(UUID tenantId) {
        return Mono.zip(
                thirdPartyRepository.countCustomersByTenantId(tenantId),
                thirdPartyRepository.countActiveCustomersByTenantId(tenantId),
                thirdPartyRepository.countInactiveCustomersByTenantId(tenantId)
        ).map(tuple -> ThirdPartyStatistics.builder()
                .total(tuple.getT1())
                .active(tuple.getT2())
                .inactive(tuple.getT3())
                .build());
    }

    // MAPPERS (Manual for now to handle the split)
    
    private ThirdPartyEntity mapToEntity(Customer domain) {
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

    private CustomerDetailsEntity mapToDetails(Customer domain, UUID id) {
        CustomerDetailsEntity details = new CustomerDetailsEntity();
        details.setId(id);
        details.setSegment(domain.getSegment() != null ? domain.getSegment().name() : null); // Enum to String if needed, or mapping
        details.setCreditLimit(domain.getCreditLimit());
        details.setAcquisitionChannel(domain.getAcquisitionChannel() != null ? domain.getAcquisitionChannel().name() : null);
        details.setCustomerVatNumber(domain.getCustomerVatNumber());
        details.setVatSubject(domain.isVatSubject());
        details.setRetailSale(domain.isRetailSale());
        details.setSemiWholesale(domain.isSemiWholesale());
        details.setWholesale(domain.isWholesale());
        details.setSuperWholesale(domain.isSuperWholesale());
        details.setOhadaType(domain.getOhadaType() != null ? domain.getOhadaType().name() : null);
        return details;
    }

    private Customer mapToDomain(ThirdPartyEntity base, CustomerDetailsEntity details) {
        return Customer.builder()
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
                .segment(safeEnum(CustomerSegment.class, details.getSegment()))
                .creditLimit(details.getCreditLimit())
                .acquisitionChannel(safeEnum(AcquisitionChannel.class, details.getAcquisitionChannel()))
                .customerVatNumber(details.getCustomerVatNumber())
                .vatSubject(details.getVatSubject() != null ? details.getVatSubject() : false)
                .retailSale(details.getRetailSale() != null ? details.getRetailSale() : false)
                .semiWholesale(details.getSemiWholesale() != null ? details.getSemiWholesale() : false)
                .wholesale(details.getWholesale() != null ? details.getWholesale() : false)
                .superWholesale(details.getSuperWholesale() != null ? details.getSuperWholesale() : false)
                .ohadaType(safeEnum(OhadaCustomerType.class, details.getOhadaType()))
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
