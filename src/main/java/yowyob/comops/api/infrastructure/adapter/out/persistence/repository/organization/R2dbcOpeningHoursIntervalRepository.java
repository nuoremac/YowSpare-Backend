package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.OpeningHoursIntervalEntity;

import java.util.UUID;

@Repository
public interface R2dbcOpeningHoursIntervalRepository extends ReactiveCrudRepository<OpeningHoursIntervalEntity, UUID> {
    Flux<OpeningHoursIntervalEntity> findByRuleId(UUID ruleId);

    @Modifying
    @Query("DELETE FROM opening_hours_intervals WHERE rule_id = :ruleId")
    Mono<Void> deleteByRuleId(UUID ruleId);
}