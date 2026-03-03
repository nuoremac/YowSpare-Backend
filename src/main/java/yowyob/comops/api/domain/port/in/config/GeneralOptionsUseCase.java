package yowyob.comops.api.domain.port.in.config;

import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.config.AgencySettings;
import yowyob.comops.api.domain.model.config.AppBusinessSettings;
import java.util.UUID;

public interface GeneralOptionsUseCase {
    // Global (Org)
    Mono<AppBusinessSettings> getGlobalSettings(UUID organizationId);

    Mono<AppBusinessSettings> updateGlobalSettings(UUID organizationId, AppBusinessSettings settings);

    // Local (Agency)
    Mono<AgencySettings> getAgencySettings(UUID agencyId);

    Mono<AgencySettings> updateAgencySettings(UUID agencyId, AgencySettings settings);
}
