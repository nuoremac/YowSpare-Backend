package yowyob.comops.spareapi.reservation.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.reservation.entity.ReservationEntity;

import java.util.UUID;

public interface ReservationRepository extends ReactiveCrudRepository<ReservationEntity, UUID> {

    @Query("""
            SELECT * FROM reservations
            WHERE tenant_id = :tenantId
              AND (:agencyId IS NULL OR agency_id = :agencyId)
              AND (:productId IS NULL OR product_id = :productId)
              AND (:status IS NULL OR status = :status)
            ORDER BY updated_at DESC
            """)
    Flux<ReservationEntity> search(UUID tenantId, UUID agencyId, UUID productId, String status);

    @Query("""
            SELECT * FROM reservations
            WHERE tenant_id = :tenantId
              AND agency_id = :agencyId
              AND status IN ('ACTIVE','PENDING')
            """)
    Flux<ReservationEntity> findActiveForAgency(UUID tenantId, UUID agencyId);

    interface ReservedAgg {
        UUID getProductId();
        Long getReservedQty();
    }

    @Query("""
            SELECT product_id, COALESCE(SUM(quantity), 0) AS reserved_qty
            FROM reservations
            WHERE tenant_id = :tenantId
              AND agency_id = :agencyId
              AND status IN ('ACTIVE','PENDING')
            GROUP BY product_id
            """)
    Flux<ReservedAgg> sumReservedByProduct(UUID tenantId, UUID agencyId);
}
