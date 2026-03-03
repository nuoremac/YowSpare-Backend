package yowyob.comops.api.domain.service.thirdparty;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.Customer;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import yowyob.comops.api.domain.port.in.thirdparty.CustomerUseCase;
import yowyob.comops.api.domain.port.out.thirdparty.CustomerRepositoryPort;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService implements CustomerUseCase {
    private final CustomerRepositoryPort customerRepository;

    @Override
    public Mono<Customer> createCustomer(Customer customer) {
        if (customer.getId() == null) {
            customer.setId(UUID.randomUUID());
        }
        customer.setCreatedAt(Instant.now());
        customer.setUpdatedAt(Instant.now());
        customer.setActive(true);
        // Add business logic for code generation if needed
        return customerRepository.save(customer);
    }

    @Override
    public Mono<Customer> updateCustomer(UUID id, Customer customer) {
        return customerRepository.findById(id)
                .flatMap(existing -> {
                    // Update logic using mapstruct or manual
                    // For now, assume customer object has updates
                    customer.setId(id);
                    customer.setUpdatedAt(Instant.now());
                    customer.setCreatedAt(existing.getCreatedAt()); 
                    return customerRepository.save(customer);
                });
    }

    @Override
    public Mono<Customer> getCustomer(UUID id) {
        return customerRepository.findById(id);
    }

    @Override
    public Flux<Customer> getAllCustomers(UUID tenantId) {
        return customerRepository.findAllByTenantId(tenantId);
    }

    @Override
    public Mono<Void> deleteCustomer(UUID id) {
        return customerRepository.deleteById(id);
    }

    @Override
    public Mono<Customer> findByBankAccountNumber(String bankAccountNumber) {
        return customerRepository.findByBankAccountNumber(bankAccountNumber);
    }

    @Override
    public Mono<Customer> findByAccountingAccount(String accountingAccount) {
        return customerRepository.findByAccountingAccount(accountingAccount);
    }

    @Override
    public Mono<Customer> defineBankAccount(UUID id, String bankAccountNumber) {
        return customerRepository.findById(id)
                .flatMap(customer -> {
                    customer.setBankAccountNumber(bankAccountNumber);
                    customer.setUpdatedAt(Instant.now());
                    return customerRepository.save(customer);
                });
    }

    @Override
    public Mono<Customer> defineAccountingAccount(UUID id, String accountingAccount) {
        return customerRepository.findById(id)
                .flatMap(customer -> {
                    customer.setAccountingAccount(accountingAccount);
                    customer.setUpdatedAt(Instant.now());
                    return customerRepository.save(customer);
                });
    }

    @Override
    public Mono<Customer> activateCustomer(UUID id) {
        return customerRepository.findById(id)
                .flatMap(customer -> {
                    customer.setActive(true);
                    customer.setUpdatedAt(Instant.now());
                    return customerRepository.save(customer);
                });
    }

    @Override
    public Mono<Customer> deactivateCustomer(UUID id) {
        return customerRepository.findById(id)
                .flatMap(customer -> {
                    customer.setActive(false);
                    customer.setUpdatedAt(Instant.now());
                    return customerRepository.save(customer);
                });
    }

    @Override
    public Mono<ThirdPartyStatistics> getCustomerStatistics(UUID tenantId) {
        return customerRepository.getStatistics(tenantId);
    }
}
