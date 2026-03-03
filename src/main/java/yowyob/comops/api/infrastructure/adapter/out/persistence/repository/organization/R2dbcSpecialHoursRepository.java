package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.SpecialOpeningHoursEntity;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface R2dbcSpecialHoursRepository extends ReactiveCrudRepository<SpecialOpeningHoursEntity, UUID> {
    @Query("SELECT * FROM special_opening_hours WHERE agency_id = :agencyId AND date >= :fromDate ORDER BY date ASC")
    Flux<SpecialOpeningHoursEntity> findFuture(UUID agencyId, LocalDate fromDate);

    Mono<SpecialOpeningHoursEntity> findByAgencyIdAndDate(UUID agencyId, LocalDate date);
}