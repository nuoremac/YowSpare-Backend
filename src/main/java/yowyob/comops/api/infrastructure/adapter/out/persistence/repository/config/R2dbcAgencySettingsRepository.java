package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.config;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.config.AgencySettingsEntity;
import java.util.UUID;

@Repository
public interface R2dbcAgencySettingsRepository extends ReactiveCrudRepository<AgencySettingsEntity, UUID> {
    Mono<AgencySettingsEntity> findByAgencyId(UUID agencyId);
}
