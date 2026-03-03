package yowyob.comops.api.domain.port.out.thirdparty;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.Customer;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import java.util.UUID;

public interface CustomerRepositoryPort {
    Mono<Customer> save(Customer customer);
    Mono<Customer> findById(UUID id);
    Flux<Customer> findAllByTenantId(UUID tenantId);
    Mono<Void> deleteById(UUID id);
    Mono<Boolean> existsByCode(String code, UUID tenantId);
    Mono<Customer> findByBankAccountNumber(String bankAccountNumber);
    Mono<Customer> findByAccountingAccount(String accountingAccount);
    Mono<ThirdPartyStatistics> getStatistics(UUID tenantId);
}
