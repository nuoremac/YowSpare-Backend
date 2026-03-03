package yowyob.comops.api.infrastructure.adapter.in.web.thirdparty;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.SalesAgent;
import yowyob.comops.api.domain.port.in.thirdparty.SalesAgentUseCase;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import yowyob.comops.api.infrastructure.config.web.TenantContextWebFilter;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales-agents")
@RequiredArgsConstructor
@Tag(name = "Sales Agent Management", description = "Operations related to sales agents")
public class SalesAgentController {
    private final SalesAgentUseCase salesAgentUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new sales agent")
    public Mono<SalesAgent> createAgent(@RequestHeader(TenantContextWebFilter.TENANT_HEADER) UUID tenantId,
                                        @RequestBody SalesAgent agent) {
        agent.setTenantId(tenantId);
        return salesAgentUseCase.createAgent(agent);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing sales agent")
    public Mono<SalesAgent> updateAgent(@PathVariable UUID id, @RequestBody SalesAgent agent) {
        return salesAgentUseCase.updateAgent(id, agent);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a sales agent by ID")
    public Mono<SalesAgent> getAgent(@PathVariable UUID id) {
        return salesAgentUseCase.getAgent(id);
    }

    @GetMapping
    @Operation(summary = "Get all sales agents for the current tenant")
    public Flux<SalesAgent> getAllAgents(@RequestHeader(TenantContextWebFilter.TENANT_HEADER) UUID tenantId) {
        return salesAgentUseCase.getAllAgents(tenantId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a sales agent")
    public Mono<Void> deleteSalesAgent(@PathVariable UUID id) {
        return salesAgentUseCase.deleteSalesAgent(id);
    }

    @GetMapping("/by-bank-account/{bankAccountNumber}")
    @Operation(summary = "Find sales agent by bank account number")
    public Mono<SalesAgent> findByBankAccountNumber(@PathVariable String bankAccountNumber) {
        return salesAgentUseCase.findByBankAccountNumber(bankAccountNumber);
    }

    @GetMapping("/by-accounting-account/{accountingAccount}")
    @Operation(summary = "Find sales agent by accounting account number")
    public Mono<SalesAgent> findByAccountingAccount(@PathVariable String accountingAccount) {
        return salesAgentUseCase.findByAccountingAccount(accountingAccount);
    }

    @PatchMapping("/{id}/bank-account")
    @Operation(summary = "Define/Update bank account number for a sales agent")
    public Mono<SalesAgent> defineBankAccount(@PathVariable UUID id, @RequestBody String bankAccountNumber) {
        return salesAgentUseCase.defineBankAccount(id, bankAccountNumber);
    }

    @PatchMapping("/{id}/accounting-account")
    @Operation(summary = "Define/Update accounting account number for a sales agent")
    public Mono<SalesAgent> defineAccountingAccount(@PathVariable UUID id, @RequestBody String accountingAccount) {
        return salesAgentUseCase.defineAccountingAccount(id, accountingAccount);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a sales agent")
    public Mono<SalesAgent> activateAgent(@PathVariable UUID id) {
        return salesAgentUseCase.activateAgent(id);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a sales agent")
    public Mono<SalesAgent> deactivateAgent(@PathVariable UUID id) {
        return salesAgentUseCase.deactivateAgent(id);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get sales agent statistics")
    public Mono<ThirdPartyStatistics> getAgentStatistics(@RequestHeader(TenantContextWebFilter.TENANT_HEADER) UUID tenantId) {
        return salesAgentUseCase.getAgentStatistics(tenantId);
    }
}
