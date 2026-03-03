package yowyob.comops.api.domain.port.in.thirdparty;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.Prospect;
import yowyob.comops.api.domain.model.thirdparty.Customer;
import yowyob.comops.api.domain.model.thirdparty.Prospect;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import java.util.UUID;

public interface ProspectUseCase {
    Mono<Prospect> createProspect(Prospect prospect);
    Mono<Prospect> updateProspect(UUID id, Prospect prospect);
    Mono<Prospect> getProspect(UUID id);
    Flux<Prospect> getAllProspects(UUID tenantId);
    Mono<Void> deleteProspect(UUID id);
    Mono<Prospect> findByBankAccountNumber(String bankAccountNumber);
    Mono<Prospect> findByAccountingAccount(String accountingAccount);
    Mono<Prospect> defineBankAccount(UUID id, String bankAccountNumber);

    Mono<Prospect> defineAccountingAccount(UUID id, String accountingAccount);
    Mono<Prospect> activateProspect(UUID id);
    Mono<Prospect> deactivateProspect(UUID id);
    Mono<ThirdPartyStatistics> getProspectStatistics(UUID tenantId);
    Mono<Customer> convertProspectToCustomer(UUID prospectId);
    Mono<Long> getProspectConversionCount(UUID tenantId);
}
