package yowyob.comops.api.domain.service.thirdparty;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.Prospect;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import yowyob.comops.api.domain.port.in.thirdparty.ProspectUseCase;
import yowyob.comops.api.domain.port.out.thirdparty.ProspectRepositoryPort;
import yowyob.comops.api.domain.model.thirdparty.Customer;
import yowyob.comops.api.domain.model.thirdparty.AcquisitionChannel;
import yowyob.comops.api.domain.port.out.thirdparty.CustomerRepositoryPort;
import java.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProspectService implements ProspectUseCase {
    private final ProspectRepositoryPort prospectRepository;
    private final CustomerRepositoryPort customerRepository;

    @Override
    public Mono<Prospect> createProspect(Prospect prospect) {
        if (prospect.getId() == null) {
            prospect.setId(UUID.randomUUID());
        }
        prospect.setCreatedAt(Instant.now());
        prospect.setUpdatedAt(Instant.now());
        prospect.setActive(true);
        return prospectRepository.save(prospect);
    }

    @Override
    public Mono<Prospect> updateProspect(UUID id, Prospect prospect) {
        return prospectRepository.findById(id)
                .flatMap(existing -> {
                    prospect.setId(id);
                    prospect.setCreatedAt(existing.getCreatedAt());
                    prospect.setUpdatedAt(Instant.now());
                    return prospectRepository.save(prospect);
                });
    }

    @Override
    public Mono<Prospect> getProspect(UUID id) {
        return prospectRepository.findById(id);
    }

    @Override
    public Flux<Prospect> getAllProspects(UUID tenantId) {
        return prospectRepository.findAllByTenantId(tenantId);
    }

    @Override
    public Mono<Void> deleteProspect(UUID id) {
        return prospectRepository.deleteById(id);
    }

    @Override
    public Mono<Prospect> findByBankAccountNumber(String bankAccountNumber) {
        return prospectRepository.findByBankAccountNumber(bankAccountNumber);
    }

    @Override
    public Mono<Prospect> findByAccountingAccount(String accountingAccount) {
        return prospectRepository.findByAccountingAccount(accountingAccount);
    }

    @Override
    public Mono<Prospect> defineBankAccount(UUID id, String bankAccountNumber) {
        return prospectRepository.findById(id)
                .flatMap(prospect -> {
                    prospect.setBankAccountNumber(bankAccountNumber);
                    prospect.setUpdatedAt(Instant.now());
                    return prospectRepository.save(prospect);
                });
    }

    @Override
    public Mono<Prospect> defineAccountingAccount(UUID id, String accountingAccount) {
        return prospectRepository.findById(id)
                .flatMap(prospect -> {
                    prospect.setAccountingAccount(accountingAccount);
                    prospect.setUpdatedAt(Instant.now());
                    return prospectRepository.save(prospect);
                });
    }

    @Override
    public Mono<Prospect> activateProspect(UUID id) {
        return prospectRepository.findById(id)
                .flatMap(prospect -> {
                    prospect.setActive(true);
                    prospect.setUpdatedAt(Instant.now());
                    return prospectRepository.save(prospect);
                });
    }

    @Override
    public Mono<Prospect> deactivateProspect(UUID id) {
        return prospectRepository.findById(id)
                .flatMap(prospect -> {
                    prospect.setActive(false);
                    prospect.setUpdatedAt(Instant.now());
                    return prospectRepository.save(prospect);
                });
    }

    @Override
    public Mono<ThirdPartyStatistics> getProspectStatistics(UUID tenantId) {
        return prospectRepository.getStatistics(tenantId);
    }

    @Override
    @Transactional
    public Mono<Customer> convertProspectToCustomer(UUID prospectId) {
        return prospectRepository.findById(prospectId)
                .flatMap(prospect -> {
                    // 1. Update Prospect
                    prospect.setConversionDate(LocalDate.now());
                    prospect.setUpdatedAt(Instant.now());
                    
                    // 2. Create Customer
                    Customer customer = Customer.builder()
                            .id(UUID.randomUUID())
                            .tenantId(prospect.getTenantId())
                            .agencyId(prospect.getAgencyId())
                            .code(prospect.getCode()) // Or generate new one? Keeping same code might be risky if unicity is global. Assuming unique per table for now. 
                            .name(prospect.getName())
                            .shortName(prospect.getShortName())
                            .description(prospect.getDescription())
                            .accountingAccount(prospect.getAccountingAccount())
                            .bankAccountNumber(prospect.getBankAccountNumber())
                            .taxNumber(prospect.getTaxNumber())
                            .tradeRegistryNumber(prospect.getTradeRegistryNumber())
                            .type(prospect.getType())
                            .businessSector(prospect.getBusinessSector())
                            .companySize(prospect.getCompanySize())
                            .email(prospect.getEmail())
                            .phoneNumber(prospect.getPhoneNumber())
                            .website(prospect.getWebsite())
                            .preferredChannel(prospect.getPreferredChannel())
                            .address(prospect.getAddress())
                            .addressComplement(prospect.getAddressComplement())
                            .postalCode(prospect.getPostalCode())
                            .city(prospect.getCity())
                            .country(prospect.getCountry())
                            .active(true)
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            // Customer specific defaults
                            .acquisitionChannel(AcquisitionChannel.PROSPECT_CONVERSION)
                            .build();

                    return prospectRepository.save(prospect)
                            .then(customerRepository.save(customer));
                });
    }

    @Override
    public Mono<Long> getProspectConversionCount(UUID tenantId) {
        return prospectRepository.countConvertedProspects(tenantId);
    }
}
