package yowyob.comops.api.domain.port.in.organization;

import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.AgencySchedule;
import yowyob.comops.api.domain.model.organization.OpeningHoursRule;
import yowyob.comops.api.domain.model.organization.SpecialOpeningHours;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface OpeningHoursUseCase {
    // Configuration
    Mono<List<OpeningHoursRule>> updateRegularSchedule(UUID agencyId, List<OpeningHoursRule> rules);

    Mono<SpecialOpeningHours> addException(SpecialOpeningHours specialHours);

    Mono<Void> removeException(UUID exceptionId);

    // Consultation
    Mono<AgencySchedule> getSchedule(UUID agencyId);

    // Logique métier
    Mono<Boolean> isOpenNow(UUID agencyId);

    Mono<Boolean> isOpenAt(UUID agencyId, ZonedDateTime dateTime);
}