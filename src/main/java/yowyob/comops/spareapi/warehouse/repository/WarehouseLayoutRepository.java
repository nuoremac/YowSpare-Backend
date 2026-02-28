package yowyob.comops.spareapi.warehouse.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.warehouse.entity.WarehouseLayoutEntity;

import java.util.UUID;

public interface WarehouseLayoutRepository extends ReactiveCrudRepository<WarehouseLayoutEntity, UUID> {
    @Query("select * from warehouse_layouts where tenant_id = :tenantId and agency_id = :agencyId limit 1")
    Mono<WarehouseLayoutEntity> findByTenantIdAndAgencyId(UUID tenantId, UUID agencyId);
}

