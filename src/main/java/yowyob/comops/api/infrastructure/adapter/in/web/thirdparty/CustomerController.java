package yowyob.comops.api.infrastructure.adapter.in.web.thirdparty;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.Customer;
import yowyob.comops.api.domain.port.in.thirdparty.CustomerUseCase;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import yowyob.comops.api.infrastructure.config.web.TenantContextWebFilter;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Operations related to customers")
public class CustomerController {
    private final CustomerUseCase customerUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new customer")
    public Mono<Customer> createCustomer(@RequestHeader(TenantContextWebFilter.TENANT_HEADER) UUID tenantId,
                                         @RequestBody Customer customer) {
        customer.setTenantId(tenantId);
        return customerUseCase.createCustomer(customer);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing customer")
    public Mono<Customer> updateCustomer(@PathVariable UUID id, @RequestBody Customer customer) {
        return customerUseCase.updateCustomer(id, customer);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a customer by ID")
    public Mono<Customer> getCustomer(@PathVariable UUID id) {
        return customerUseCase.getCustomer(id);
    }

    @GetMapping
    @Operation(summary = "Get all customers for the current tenant")
    public Flux<Customer> getAllCustomers(@RequestHeader(TenantContextWebFilter.TENANT_HEADER) UUID tenantId) {
        return customerUseCase.getAllCustomers(tenantId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a customer")
    public Mono<Void> deleteCustomer(@PathVariable UUID id) {
        return customerUseCase.deleteCustomer(id);
    }

    @GetMapping("/by-bank-account/{bankAccountNumber}")
    @Operation(summary = "Find customer by bank account number")
    public Mono<Customer> findByBankAccountNumber(@PathVariable String bankAccountNumber) {
        return customerUseCase.findByBankAccountNumber(bankAccountNumber);
    }

    @GetMapping("/by-accounting-account/{accountingAccount}")
    @Operation(summary = "Find customer by accounting account number")
    public Mono<Customer> findByAccountingAccount(@PathVariable String accountingAccount) {
        return customerUseCase.findByAccountingAccount(accountingAccount);
    }

    @PatchMapping("/{id}/bank-account")
    @Operation(summary = "Define/Update bank account number for a customer")
    public Mono<Customer> defineBankAccount(@PathVariable UUID id, @RequestBody String bankAccountNumber) {
        return customerUseCase.defineBankAccount(id, bankAccountNumber);
    }

    @PatchMapping("/{id}/accounting-account")
    @Operation(summary = "Define/Update accounting account number for a customer")
    public Mono<Customer> defineAccountingAccount(@PathVariable UUID id, @RequestBody String accountingAccount) {
        return customerUseCase.defineAccountingAccount(id, accountingAccount);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a customer")
    public Mono<Customer> activateCustomer(@PathVariable UUID id) {
        return customerUseCase.activateCustomer(id);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a customer")
    public Mono<Customer> deactivateCustomer(@PathVariable UUID id) {
        return customerUseCase.deactivateCustomer(id);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get customer statistics")
    public Mono<ThirdPartyStatistics> getCustomerStatistics(@RequestHeader(TenantContextWebFilter.TENANT_HEADER) UUID tenantId) {
        return customerUseCase.getCustomerStatistics(tenantId);
    }
}
