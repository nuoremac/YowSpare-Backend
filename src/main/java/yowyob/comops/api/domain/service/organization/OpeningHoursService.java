package yowyob.comops.api.domain.service.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.Agency;
import yowyob.comops.api.domain.model.organization.AgencySchedule;
import yowyob.comops.api.domain.model.organization.OpeningHoursRule;
import yowyob.comops.api.domain.model.organization.SpecialOpeningHours;
import yowyob.comops.api.domain.model.shared.TimeInterval;
import yowyob.comops.api.domain.port.in.organization.OpeningHoursUseCase;
import yowyob.comops.api.domain.port.out.organization.AgencyRepositoryPort;
import yowyob.comops.api.domain.port.out.organization.OpeningHoursRepositoryPort;
import java.time.*;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpeningHoursService implements OpeningHoursUseCase {
    private final OpeningHoursRepositoryPort repository;
    private final AgencyRepositoryPort agencyRepository;

    @Override
    @Transactional
    public Mono<List<OpeningHoursRule>> updateRegularSchedule(UUID agencyId, List<OpeningHoursRule> rules) {
        // Validation et association à l'agence
        rules.forEach(rule -> {
            rule.setAgencyId(agencyId);
            if (!rule.isClosed() && (rule.getIntervals() == null || rule.getIntervals().isEmpty())) {
                rule.setClosed(true); // Si ouvert sans heures, on considère fermé
            }
        });

        // Sauvegarde de chaque règle
        return Flux.fromIterable(rules)
                .flatMap(repository::saveRule)
                .collectList()
                .map(savedList -> savedList.stream()
                        .sorted(Comparator.comparing(OpeningHoursRule::getDayOfWeek))
                        .collect(Collectors.toList()));
    }

    @Override
    public Mono<SpecialOpeningHours> addException(SpecialOpeningHours specialHours) {
        if (specialHours.getDate().isBefore(LocalDate.now())) {
            return Mono.error(new IllegalArgumentException("Cannot add exception in the past"));
        }
        return repository.saveException(specialHours);
    }

    @Override
    public Mono<Void> removeException(UUID exceptionId) {
        return repository.deleteException(exceptionId);
    }

    @Override
    public Mono<AgencySchedule> getSchedule(UUID agencyId) {
        Mono<Agency> agencyMono = agencyRepository.findById(agencyId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Agency not found")));

        Mono<List<OpeningHoursRule>> rulesMono = repository.findRulesByAgencyId(agencyId)
                .sort(Comparator.comparing(OpeningHoursRule::getDayOfWeek))
                .collectList();

        Mono<List<SpecialOpeningHours>> exceptionsMono = repository.findFutureExceptions(agencyId, LocalDate.now())
                .collectList();

        return Mono.zip(agencyMono, rulesMono, exceptionsMono)
                .map(tuple -> AgencySchedule.builder()
                        .agencyName(tuple.getT1().getName())
                        .timezone(tuple.getT1().getTimezone() != null ? tuple.getT1().getTimezone() : "UTC")
                        .regularRules(tuple.getT2())
                        .upcomingExceptions(tuple.getT3())
                        .build());
    }

    @Override
    public Mono<Boolean> isOpenNow(UUID agencyId) {
        return agencyRepository.findById(agencyId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Agency not found")))
                .flatMap(agency -> {
                    String tz = agency.getTimezone() != null ? agency.getTimezone() : "UTC";
                    ZonedDateTime nowInAgency = ZonedDateTime.now(ZoneId.of(tz));
                    return isOpenAt(agencyId, nowInAgency);
                });
    }

    @Override
    public Mono<Boolean> isOpenAt(UUID agencyId, ZonedDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();

        // 1. Vérifier les exceptions (prioritaires)
        return repository.findExceptionByDate(agencyId, date)
                .map(exception -> checkIntervals(exception.getIntervals(), time, exception.isClosed()))
                .switchIfEmpty(Mono.defer(() -> {
                    // 2. Si pas d'exception, vérifier la règle régulière
                    return repository.findRulesByAgencyId(agencyId)
                            .filter(r -> r.getDayOfWeek() == dateTime.getDayOfWeek())
                            .next()
                            .map(rule -> checkIntervals(rule.getIntervals(), time, rule.isClosed()))
                            .defaultIfEmpty(false); // Par défaut, fermé si aucune règle n'est définie
                }));
    }

    private boolean checkIntervals(List<TimeInterval> intervals, LocalTime time, boolean isClosed) {
        if (isClosed)
            return false;
        if (intervals == null)
            return false;

        for (TimeInterval interval : intervals) {
            if (!time.isBefore(interval.getStartTime()) && time.isBefore(interval.getEndTime())) {
                return true;
            }
        }
        return false;
    }
}
