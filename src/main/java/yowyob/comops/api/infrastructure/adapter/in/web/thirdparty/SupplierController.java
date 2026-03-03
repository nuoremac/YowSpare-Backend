package yowyob.comops.api.infrastructure.adapter.in.web.thirdparty;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.Supplier;
import yowyob.comops.api.domain.port.in.thirdparty.SupplierUseCase;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import yowyob.comops.api.infrastructure.config.web.TenantContextWebFilter;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
@Tag(name = "Supplier Management", description = "Operations related to suppliers")
public class SupplierController {
    private final SupplierUseCase supplierUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new supplier")
    public Mono<Supplier> createSupplier(@RequestHeader(TenantContextWebFilter.TENANT_HEADER) UUID tenantId,
                                         @RequestBody Supplier supplier) {
        supplier.setTenantId(tenantId);
        return supplierUseCase.createSupplier(supplier);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing supplier")
    public Mono<Supplier> updateSupplier(@PathVariable UUID id, @RequestBody Supplier supplier) {
        return supplierUseCase.updateSupplier(id, supplier);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a supplier by ID")
    public Mono<Supplier> getSupplier(@PathVariable UUID id) {
        return supplierUseCase.getSupplier(id);
    }

    @GetMapping
    @Operation(summary = "Get all suppliers for the current tenant")
    public Flux<Supplier> getAllSuppliers(@RequestHeader(TenantContextWebFilter.TENANT_HEADER) UUID tenantId) {
        return supplierUseCase.getAllSuppliers(tenantId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a supplier")
    public Mono<Void> deleteSupplier(@PathVariable UUID id) {
        return supplierUseCase.deleteSupplier(id);
    }

    @GetMapping("/by-bank-account/{bankAccountNumber}")
    @Operation(summary = "Find supplier by bank account number")
    public Mono<Supplier> findByBankAccountNumber(@PathVariable String bankAccountNumber) {
        return supplierUseCase.findByBankAccountNumber(bankAccountNumber);
    }

    @GetMapping("/by-accounting-account/{accountingAccount}")
    @Operation(summary = "Find supplier by accounting account number")
    public Mono<Supplier> findByAccountingAccount(@PathVariable String accountingAccount) {
        return supplierUseCase.findByAccountingAccount(accountingAccount);
    }

    @PatchMapping("/{id}/bank-account")
    @Operation(summary = "Define/Update bank account number for a supplier")
    public Mono<Supplier> defineBankAccount(@PathVariable UUID id, @RequestBody String bankAccountNumber) {
        return supplierUseCase.defineBankAccount(id, bankAccountNumber);
    }

    @PatchMapping("/{id}/accounting-account")
    @Operation(summary = "Define/Update accounting account number for a supplier")
    public Mono<Supplier> defineAccountingAccount(@PathVariable UUID id, @RequestBody String accountingAccount) {
        return supplierUseCase.defineAccountingAccount(id, accountingAccount);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a supplier")
    public Mono<Supplier> activateSupplier(@PathVariable UUID id) {
        return supplierUseCase.activateSupplier(id);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a supplier")
    public Mono<Supplier> deactivateSupplier(@PathVariable UUID id) {
        return supplierUseCase.deactivateSupplier(id);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get supplier statistics")
    public Mono<ThirdPartyStatistics> getSupplierStatistics(@RequestHeader(TenantContextWebFilter.TENANT_HEADER) UUID tenantId) {
        return supplierUseCase.getSupplierStatistics(tenantId);
    }
}
