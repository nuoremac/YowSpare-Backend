package yowyob.comops.api.domain.port.in.thirdparty;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.Supplier;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import java.util.UUID;

public interface SupplierUseCase {
    Mono<Supplier> createSupplier(Supplier supplier);
    Mono<Supplier> updateSupplier(UUID id, Supplier supplier);
    Mono<Supplier> getSupplier(UUID id);
    Flux<Supplier> getAllSuppliers(UUID tenantId);
    Mono<Void> deleteSupplier(UUID id);
    Mono<Supplier> findByBankAccountNumber(String bankAccountNumber);
    Mono<Supplier> findByAccountingAccount(String accountingAccount);
    Mono<Supplier> defineBankAccount(UUID id, String bankAccountNumber);

    Mono<Supplier> defineAccountingAccount(UUID id, String accountingAccount);
    Mono<Supplier> activateSupplier(UUID id);
    Mono<Supplier> deactivateSupplier(UUID id);
    Mono<ThirdPartyStatistics> getSupplierStatistics(UUID tenantId);
}
