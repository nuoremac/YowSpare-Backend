package yowyob.comops.api.domain.port.out.organization;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.OpeningHoursRule;
import yowyob.comops.api.domain.model.organization.SpecialOpeningHours;
import java.time.LocalDate;
import java.util.UUID;

public interface OpeningHoursRepositoryPort {
    // Regular Rules
    Mono<OpeningHoursRule> saveRule(OpeningHoursRule rule);

    Flux<OpeningHoursRule> findRulesByAgencyId(UUID agencyId);

    // Special Exceptions
    Mono<SpecialOpeningHours> saveException(SpecialOpeningHours exception);

    Flux<SpecialOpeningHours> findFutureExceptions(UUID agencyId, LocalDate fromDate);

    Mono<SpecialOpeningHours> findExceptionByDate(UUID agencyId, LocalDate date);

    Mono<Void> deleteException(UUID id);
}