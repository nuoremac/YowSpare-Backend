package yowyob.comops.api.infrastructure.adapter.out.persistence.adapter.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.OpeningHoursRule;
import yowyob.comops.api.domain.model.organization.SpecialOpeningHours;
import yowyob.comops.api.domain.model.shared.TimeInterval;
import yowyob.comops.api.domain.port.out.organization.OpeningHoursRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.OpeningHoursIntervalEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.OpeningHoursRuleEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.SpecialOpeningHoursEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.SpecialOpeningHoursIntervalEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization.R2dbcOpeningHoursIntervalRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization.R2dbcOpeningHoursRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization.R2dbcSpecialHoursIntervalRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization.R2dbcSpecialHoursRepository;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OpeningHoursPersistenceAdapter implements OpeningHoursRepositoryPort {

    private final R2dbcOpeningHoursRepository ruleRepository;
    private final R2dbcOpeningHoursIntervalRepository ruleIntervalRepository;
    private final R2dbcSpecialHoursRepository specialRepository;
    private final R2dbcSpecialHoursIntervalRepository specialIntervalRepository;

    @Override
    @Transactional
    public Mono<OpeningHoursRule> saveRule(OpeningHoursRule rule) {
        // Convention BDD : 0=Lundi, 6=Dimanche. Java DayOfWeek.MONDAY = 1.
        int dayIndex = rule.getDayOfWeek().getValue() - 1;

        return ruleRepository.findByAgencyAndDay(rule.getAgencyId(), dayIndex)
                .defaultIfEmpty(new OpeningHoursRuleEntity()) // Nouvelle entité si non trouvée
                .flatMap(entity -> {
                    entity.setAgencyId(rule.getAgencyId());
                    entity.setDayOfWeek(dayIndex);
                    entity.setIsClosed(rule.isClosed());
                    if (entity.getId() == null) {
                        entity.setId(null);
                        entity.setCreatedAt(Instant.now());
                    }
                    entity.setUpdatedAt(Instant.now());

                    return ruleRepository.save(entity);
                })
                .flatMap(savedRule -> {
                    // Supprimer anciens intervalles et recréer les nouveaux
                    return ruleIntervalRepository.deleteByRuleId(savedRule.getId())
                            .then(Mono.defer(() -> {
                                if (rule.getIntervals() == null || rule.getIntervals().isEmpty()) {
                                    return Mono.empty();
                                }
                                List<OpeningHoursIntervalEntity> intervals = rule.getIntervals().stream()
                                        .map(i -> new OpeningHoursIntervalEntity(null, savedRule.getId(), i.getStartTime(), i.getEndTime()))
                                        .collect(Collectors.toList());
                                return ruleIntervalRepository.saveAll(intervals).collectList();
                            }))
                            .map(savedIntervals -> mapToDomain(savedRule, savedIntervals));
                });
    }

    @Override
    public Flux<OpeningHoursRule> findRulesByAgencyId(UUID agencyId) {
        return ruleRepository.findByAgencyId(agencyId)
                .flatMap(ruleEntity -> ruleIntervalRepository.findByRuleId(ruleEntity.getId())
                        .collectList()
                        .map(intervals -> mapToDomain(ruleEntity, intervals)));
    }

    @Override
    @Transactional
    public Mono<SpecialOpeningHours> saveException(SpecialOpeningHours exception) {
        return specialRepository.findByAgencyIdAndDate(exception.getAgencyId(), exception.getDate())
                .defaultIfEmpty(new SpecialOpeningHoursEntity())
                .flatMap(entity -> {
                    entity.setAgencyId(exception.getAgencyId());
                    entity.setDate(exception.getDate());
                    entity.setLabel(exception.getLabel());
                    entity.setIsClosed(exception.isClosed());

                    if (entity.getId() == null) {
                        entity.setId(null);
                        entity.setCreatedAt(Instant.now());
                    }
                    entity.setUpdatedAt(Instant.now());
                    return specialRepository.save(entity);
                })
                .flatMap(savedSpecial -> {
                    return specialIntervalRepository.deleteBySpecialId(savedSpecial.getId())
                            .then(Mono.defer(() -> {
                                if (exception.getIntervals() == null || exception.getIntervals().isEmpty()) {
                                    return Mono.empty();
                                }
                                List<SpecialOpeningHoursIntervalEntity> intervals = exception.getIntervals().stream()
                                        .map(i -> new SpecialOpeningHoursIntervalEntity(null, savedSpecial.getId(), i.getStartTime(), i.getEndTime()))
                                        .collect(Collectors.toList());
                                return specialIntervalRepository.saveAll(intervals).collectList();
                            }))
                            .map(savedIntervals -> mapToDomain(savedSpecial, savedIntervals));
                });
    }

    @Override
    public Flux<SpecialOpeningHours> findFutureExceptions(UUID agencyId, LocalDate fromDate) {
        return specialRepository.findFuture(agencyId, fromDate)
                .flatMap(entity -> specialIntervalRepository.findBySpecialId(entity.getId())
                        .collectList()
                        .map(intervals -> mapToDomain(entity, intervals)));
    }

    @Override
    public Mono<SpecialOpeningHours> findExceptionByDate(UUID agencyId, LocalDate date) {
        return specialRepository.findByAgencyIdAndDate(agencyId, date)
                .flatMap(entity -> specialIntervalRepository.findBySpecialId(entity.getId())
                        .collectList()
                        .map(intervals -> mapToDomain(entity, intervals)));
    }

    @Override
    @Transactional
    public Mono<Void> deleteException(UUID id) {
        return specialIntervalRepository.deleteBySpecialId(id)
                .then(specialRepository.deleteById(id));
    }

    // --- MAPPERS INTERNES ---

    private OpeningHoursRule mapToDomain(OpeningHoursRuleEntity entity, List<OpeningHoursIntervalEntity> intervals) {
        List<TimeInterval> domainIntervals = intervals.stream()
                .map(i -> new TimeInterval(i.getStartTime(), i.getEndTime()))
                .collect(Collectors.toList());

        DayOfWeek day = DayOfWeek.of(entity.getDayOfWeek() + 1);

        return OpeningHoursRule.builder()
                .id(entity.getId())
                .agencyId(entity.getAgencyId())
                .dayOfWeek(day)
                .isClosed(Boolean.TRUE.equals(entity.getIsClosed()))
                .intervals(domainIntervals)
                .build();
    }

    private SpecialOpeningHours mapToDomain(SpecialOpeningHoursEntity entity, List<SpecialOpeningHoursIntervalEntity> intervals) {
        List<TimeInterval> domainIntervals = intervals.stream()
                .map(i -> new TimeInterval(i.getStartTime(), i.getEndTime()))
                .collect(Collectors.toList());

        return SpecialOpeningHours.builder()
                .id(entity.getId())
                .agencyId(entity.getAgencyId())
                .date(entity.getDate())
                .label(entity.getLabel())
                .isClosed(Boolean.TRUE.equals(entity.getIsClosed()))
                .intervals(domainIntervals)
                .build();
    }
}