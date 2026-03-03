package yowyob.comops.api.domain.port.out.thirdparty;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.Prospect;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import java.util.UUID;

public interface ProspectRepositoryPort {
    Mono<Prospect> save(Prospect prospect);
    Mono<Prospect> findById(UUID id);
    Flux<Prospect> findAllByTenantId(UUID tenantId);
    Mono<Void> deleteById(UUID id);
    Mono<Boolean> existsByCode(String code, UUID tenantId);
    Mono<Prospect> findByBankAccountNumber(String bankAccountNumber);
    Mono<Prospect> findByAccountingAccount(String accountingAccount);
    Mono<ThirdPartyStatistics> getStatistics(UUID tenantId);
    Mono<Long> countConvertedProspects(UUID tenantId);
}
