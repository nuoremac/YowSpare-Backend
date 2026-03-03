package yowyob.comops.api.domain.service.thirdparty;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.Supplier;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import yowyob.comops.api.domain.port.in.thirdparty.SupplierUseCase;
import yowyob.comops.api.domain.port.out.thirdparty.SupplierRepositoryPort;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService implements SupplierUseCase {
    private final SupplierRepositoryPort supplierRepository;

    @Override
    public Mono<Supplier> createSupplier(Supplier supplier) {
        if (supplier.getId() == null) {
            supplier.setId(UUID.randomUUID());
        }
        supplier.setCreatedAt(Instant.now());
        supplier.setUpdatedAt(Instant.now());
        supplier.setActive(true);
        return supplierRepository.save(supplier);
    }

    @Override
    public Mono<Supplier> updateSupplier(UUID id, Supplier supplier) {
        return supplierRepository.findById(id)
                .flatMap(existing -> {
                    supplier.setId(id);
                    supplier.setCreatedAt(existing.getCreatedAt());
                    supplier.setUpdatedAt(Instant.now());
                    return supplierRepository.save(supplier);
                });
    }

    @Override
    public Mono<Supplier> getSupplier(UUID id) {
        return supplierRepository.findById(id);
    }

    @Override
    public Flux<Supplier> getAllSuppliers(UUID tenantId) {
        return supplierRepository.findAllByTenantId(tenantId);
    }

    @Override
    public Mono<Void> deleteSupplier(UUID id) {
        return supplierRepository.deleteById(id);
    }

    @Override
    public Mono<Supplier> findByBankAccountNumber(String bankAccountNumber) {
        return supplierRepository.findByBankAccountNumber(bankAccountNumber);
    }

    @Override
    public Mono<Supplier> findByAccountingAccount(String accountingAccount) {
        return supplierRepository.findByAccountingAccount(accountingAccount);
    }

    @Override
    public Mono<Supplier> defineBankAccount(UUID id, String bankAccountNumber) {
        return supplierRepository.findById(id)
                .flatMap(supplier -> {
                    supplier.setBankAccountNumber(bankAccountNumber);
                    supplier.setUpdatedAt(Instant.now());
                    return supplierRepository.save(supplier);
                });
    }

    @Override
    public Mono<Supplier> defineAccountingAccount(UUID id, String accountingAccount) {
        return supplierRepository.findById(id)
                .flatMap(supplier -> {
                    supplier.setAccountingAccount(accountingAccount);
                    supplier.setUpdatedAt(Instant.now());
                    return supplierRepository.save(supplier);
                });
    }

    @Override
    public Mono<Supplier> activateSupplier(UUID id) {
        return supplierRepository.findById(id)
                .flatMap(supplier -> {
                    supplier.setActive(true);
                    supplier.setUpdatedAt(Instant.now());
                    return supplierRepository.save(supplier);
                });
    }

    @Override
    public Mono<Supplier> deactivateSupplier(UUID id) {
        return supplierRepository.findById(id)
                .flatMap(supplier -> {
                    supplier.setActive(false);
                    supplier.setUpdatedAt(Instant.now());
                    return supplierRepository.save(supplier);
                });
    }

    @Override
    public Mono<ThirdPartyStatistics> getSupplierStatistics(UUID tenantId) {
        return supplierRepository.getStatistics(tenantId);
    }
}
