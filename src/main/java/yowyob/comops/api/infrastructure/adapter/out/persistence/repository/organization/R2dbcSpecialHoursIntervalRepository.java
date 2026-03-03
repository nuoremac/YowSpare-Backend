package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.SpecialOpeningHoursIntervalEntity;

import java.util.UUID;

@Repository
public interface R2dbcSpecialHoursIntervalRepository
        extends ReactiveCrudRepository<SpecialOpeningHoursIntervalEntity, UUID> {
    Flux<SpecialOpeningHoursIntervalEntity> findBySpecialId(UUID specialId);

    @Modifying
    @Query("DELETE FROM special_opening_hours_intervals WHERE special_id = :specialId")
    Mono<Void> deleteBySpecialId(UUID specialId);
}