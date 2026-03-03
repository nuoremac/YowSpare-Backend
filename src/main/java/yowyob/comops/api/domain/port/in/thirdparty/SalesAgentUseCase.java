package yowyob.comops.api.domain.port.in.thirdparty;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.SalesAgent;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import java.util.UUID;

public interface SalesAgentUseCase {
    Mono<SalesAgent> createAgent(SalesAgent agent);
    Mono<SalesAgent> updateAgent(UUID id, SalesAgent agent);
    Mono<SalesAgent> getAgent(UUID id);
    Flux<SalesAgent> getAllAgents(UUID tenantId);
    Mono<Void> deleteSalesAgent(UUID id);
    Mono<SalesAgent> findByBankAccountNumber(String bankAccountNumber);
    Mono<SalesAgent> findByAccountingAccount(String accountingAccount);
    Mono<SalesAgent> defineBankAccount(UUID id, String bankAccountNumber);

    Mono<SalesAgent> defineAccountingAccount(UUID id, String accountingAccount);
    Mono<SalesAgent> activateAgent(UUID id);
    Mono<SalesAgent> deactivateAgent(UUID id);
    Mono<ThirdPartyStatistics> getAgentStatistics(UUID tenantId);
}
