package yowyob.comops.api.infrastructure.adapter.in.web.thirdparty;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.Prospect;
import yowyob.comops.api.domain.model.thirdparty.Customer;
import yowyob.comops.api.domain.port.in.thirdparty.ProspectUseCase;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import yowyob.comops.api.infrastructure.config.web.TenantContextWebFilter;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/prospects")
@RequiredArgsConstructor
@Tag(name = "Prospect Management", description = "Operations related to prospects")
public class ProspectController {
    private final ProspectUseCase prospectUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new prospect")
    public Mono<Prospect> createProspect(@RequestHeader(TenantContextWebFilter.TENANT_HEADER) UUID tenantId,
                                         @RequestBody Prospect prospect) {
        prospect.setTenantId(tenantId);
        return prospectUseCase.createProspect(prospect);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing prospect")
    public Mono<Prospect> updateProspect(@PathVariable UUID id, @RequestBody Prospect prospect) {
        return prospectUseCase.updateProspect(id, prospect);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a prospect by ID")
    public Mono<Prospect> getProspect(@PathVariable UUID id) {
        return prospectUseCase.getProspect(id);
    }

    @GetMapping
    @Operation(summary = "Get all prospects for the current tenant")
    public Flux<Prospect> getAllProspects(@RequestHeader(TenantContextWebFilter.TENANT_HEADER) UUID tenantId) {
        return prospectUseCase.getAllProspects(tenantId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a prospect")
    public Mono<Void> deleteProspect(@PathVariable UUID id) {
        return prospectUseCase.deleteProspect(id);
    }

    @GetMapping("/by-bank-account/{bankAccountNumber}")
    @Operation(summary = "Find prospect by bank account number")
    public Mono<Prospect> findByBankAccountNumber(@PathVariable String bankAccountNumber) {
        return prospectUseCase.findByBankAccountNumber(bankAccountNumber);
    }

    @GetMapping("/by-accounting-account/{accountingAccount}")
    @Operation(summary = "Find prospect by accounting account number")
    public Mono<Prospect> findByAccountingAccount(@PathVariable String accountingAccount) {
        return prospectUseCase.findByAccountingAccount(accountingAccount);
    }

    @PatchMapping("/{id}/bank-account")
    @Operation(summary = "Define/Update bank account number for a prospect")
    public Mono<Prospect> defineBankAccount(@PathVariable UUID id, @RequestBody String bankAccountNumber) {
        return prospectUseCase.defineBankAccount(id, bankAccountNumber);
    }

    @PatchMapping("/{id}/accounting-account")
    @Operation(summary = "Define/Update accounting account number for a prospect")
    public Mono<Prospect> defineAccountingAccount(@PathVariable UUID id, @RequestBody String accountingAccount) {
        return prospectUseCase.defineAccountingAccount(id, accountingAccount);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a prospect")
    public Mono<Prospect> activateProspect(@PathVariable UUID id) {
        return prospectUseCase.activateProspect(id);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a prospect")
    public Mono<Prospect> deactivateProspect(@PathVariable UUID id) {
        return prospectUseCase.deactivateProspect(id);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get prospect statistics")
    public Mono<ThirdPartyStatistics> getProspectStatistics(@RequestHeader(TenantContextWebFilter.TENANT_HEADER) UUID tenantId) {
        return prospectUseCase.getProspectStatistics(tenantId);
    }

    @PostMapping("/{id}/convert")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Convert a prospect to a customer")
    public Mono<Customer> convertProspectToCustomer(@PathVariable UUID id) {
        return prospectUseCase.convertProspectToCustomer(id);
    }

    @GetMapping("/statistics/conversions")
    @Operation(summary = "Get count of converted prospects")
    public Mono<Long> getProspectConversionCount(@RequestHeader(TenantContextWebFilter.TENANT_HEADER) UUID tenantId) {
        return prospectUseCase.getProspectConversionCount(tenantId);
    }
}
