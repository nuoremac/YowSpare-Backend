package yowyob.comops.api.domain.port.out.thirdparty;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.SalesAgent;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import java.util.UUID;

public interface SalesAgentRepositoryPort {
    Mono<SalesAgent> save(SalesAgent agent);
    Mono<SalesAgent> findById(UUID id);
    Flux<SalesAgent> findAllByTenantId(UUID tenantId);
    Mono<Void> deleteById(UUID id);
    Mono<Boolean> existsByCode(String code, UUID tenantId);
    Mono<SalesAgent> findByBankAccountNumber(String bankAccountNumber);
    Mono<SalesAgent> findByAccountingAccount(String accountingAccount);
    Mono<ThirdPartyStatistics> getStatistics(UUID tenantId);
}
