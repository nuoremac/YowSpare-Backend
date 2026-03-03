package yowyob.comops.api.domain.port.in.thirdparty;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.Customer;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import java.util.UUID;

public interface CustomerUseCase {
    Mono<Customer> createCustomer(Customer customer);
    Mono<Customer> updateCustomer(UUID id, Customer customer);
    Mono<Customer> getCustomer(UUID id);
    Flux<Customer> getAllCustomers(UUID tenantId);
    Mono<Void> deleteCustomer(UUID id);
    Mono<Customer> findByBankAccountNumber(String bankAccountNumber);
    Mono<Customer> findByAccountingAccount(String accountingAccount);
    Mono<Customer> defineBankAccount(UUID id, String bankAccountNumber);

    Mono<Customer> defineAccountingAccount(UUID id, String accountingAccount);
    Mono<Customer> activateCustomer(UUID id);
    Mono<Customer> deactivateCustomer(UUID id);
    Mono<ThirdPartyStatistics> getCustomerStatistics(UUID tenantId);
}
