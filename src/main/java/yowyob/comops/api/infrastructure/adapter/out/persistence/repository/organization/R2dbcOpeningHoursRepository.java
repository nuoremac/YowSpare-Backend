package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.OpeningHoursRuleEntity;

import java.util.UUID;

@Repository
public interface R2dbcOpeningHoursRepository extends ReactiveCrudRepository<OpeningHoursRuleEntity, UUID> {
    Flux<OpeningHoursRuleEntity> findByAgencyId(UUID agencyId);

    Mono<Void> deleteByAgencyId(UUID agencyId);

    @Query("SELECT * FROM opening_hours_rules WHERE agency_id = :agencyId AND day_of_week = :dayOfWeek")
    Mono<OpeningHoursRuleEntity> findByAgencyAndDay(UUID agencyId, Integer dayOfWeek);
}