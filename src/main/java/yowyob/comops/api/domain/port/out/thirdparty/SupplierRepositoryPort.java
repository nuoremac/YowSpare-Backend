package yowyob.comops.api.domain.port.out.thirdparty;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.Supplier;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import java.util.UUID;

public interface SupplierRepositoryPort {
    Mono<Supplier> save(Supplier supplier);
    Mono<Supplier> findById(UUID id);
    Flux<Supplier> findAllByTenantId(UUID tenantId);
    Mono<Void> deleteById(UUID id);
    Mono<Boolean> existsByCode(String code, UUID tenantId);
    Mono<Supplier> findByBankAccountNumber(String bankAccountNumber);
    Mono<Supplier> findByAccountingAccount(String accountingAccount);
    Mono<ThirdPartyStatistics> getStatistics(UUID tenantId);
}
