package yowyob.comops.api.domain.port.out.config;

import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.config.AgencySettings;
import yowyob.comops.api.domain.model.config.AppBusinessSettings;
import java.util.UUID;

public interface GeneralOptionsRepositoryPort {
    // Global
    Mono<AppBusinessSettings> findByOrganizationId(UUID organizationId);

    Mono<AppBusinessSettings> saveGlobal(AppBusinessSettings settings);

    // Local
    Mono<AgencySettings> findByAgencyId(UUID agencyId);

    Mono<AgencySettings> saveLocal(AgencySettings settings);
}
