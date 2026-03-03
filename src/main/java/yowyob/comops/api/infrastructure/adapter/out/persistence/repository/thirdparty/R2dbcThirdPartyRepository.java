package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.thirdparty;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.thirdparty.ThirdPartyEntity;
import java.util.UUID;

@Repository
public interface R2dbcThirdPartyRepository extends ReactiveCrudRepository<ThirdPartyEntity, UUID> {
    Flux<ThirdPartyEntity> findByTenantId(UUID tenantId);
    Mono<ThirdPartyEntity> findByBankAccountNumber(String bankAccountNumber);
    Mono<ThirdPartyEntity> findByAccountingAccount(String accountingAccount);

    // Customer Stats
    @Query("SELECT COUNT(*) FROM clients c JOIN tiers t ON c.id = t.id WHERE t.tenant_id = :tenantId")
    Mono<Long> countCustomersByTenantId(UUID tenantId);

    @Query("SELECT COUNT(*) FROM clients c JOIN tiers t ON c.id = t.id WHERE t.tenant_id = :tenantId AND t.active = true")
    Mono<Long> countActiveCustomersByTenantId(UUID tenantId);

    @Query("SELECT COUNT(*) FROM clients c JOIN tiers t ON c.id = t.id WHERE t.tenant_id = :tenantId AND (t.active = false OR t.active IS NULL)")
    Mono<Long> countInactiveCustomersByTenantId(UUID tenantId);

    // Supplier Stats
    @Query("SELECT COUNT(*) FROM fournisseurs f JOIN tiers t ON f.id = t.id WHERE t.tenant_id = :tenantId")
    Mono<Long> countSuppliersByTenantId(UUID tenantId);

    @Query("SELECT COUNT(*) FROM fournisseurs f JOIN tiers t ON f.id = t.id WHERE t.tenant_id = :tenantId AND t.active = true")
    Mono<Long> countActiveSuppliersByTenantId(UUID tenantId);

    @Query("SELECT COUNT(*) FROM fournisseurs f JOIN tiers t ON f.id = t.id WHERE t.tenant_id = :tenantId AND (t.active = false OR t.active IS NULL)")
    Mono<Long> countInactiveSuppliersByTenantId(UUID tenantId);

    // Prospect Stats
    @Query("SELECT COUNT(*) FROM prospects p JOIN tiers t ON p.id = t.id WHERE t.tenant_id = :tenantId")
    Mono<Long> countProspectsByTenantId(UUID tenantId);

    @Query("SELECT COUNT(*) FROM prospects p JOIN tiers t ON p.id = t.id WHERE t.tenant_id = :tenantId AND t.active = true")
    Mono<Long> countActiveProspectsByTenantId(UUID tenantId);

    @Query("SELECT COUNT(*) FROM prospects p JOIN tiers t ON p.id = t.id WHERE t.tenant_id = :tenantId AND (t.active = false OR t.active IS NULL)")
    Mono<Long> countInactiveProspectsByTenantId(UUID tenantId);

    // SalesAgent Stats
    @Query("SELECT COUNT(*) FROM commerciaux sa JOIN tiers t ON sa.id = t.id WHERE t.tenant_id = :tenantId")
    Mono<Long> countSalesAgentsByTenantId(UUID tenantId);

    @Query("SELECT COUNT(*) FROM commerciaux sa JOIN tiers t ON sa.id = t.id WHERE t.tenant_id = :tenantId AND t.active = true")
    Mono<Long> countActiveSalesAgentsByTenantId(UUID tenantId);

    @Query("SELECT COUNT(*) FROM commerciaux sa JOIN tiers t ON sa.id = t.id WHERE t.tenant_id = :tenantId AND (t.active = false OR t.active IS NULL)")
    Mono<Long> countInactiveSalesAgentsByTenantId(UUID tenantId);
    @Query("SELECT COUNT(*) FROM prospects p JOIN tiers t ON p.id = t.id WHERE t.tenant_id = :tenantId AND p.date_conversion IS NOT NULL")
    Mono<Long> countConvertedProspectsByTenantId(UUID tenantId);
}
