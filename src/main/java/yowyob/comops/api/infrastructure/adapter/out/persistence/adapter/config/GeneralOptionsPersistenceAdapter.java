package yowyob.comops.api.infrastructure.adapter.out.persistence.adapter.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.config.AgencySettings;
import yowyob.comops.api.domain.model.config.AppBusinessSettings;
import yowyob.comops.api.domain.port.out.config.GeneralOptionsRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.config.AgencySettingsEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.config.AppBusinessSettingsEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.mapper.config.GeneralOptionsMapper;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.config.R2dbcAgencySettingsRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.config.R2dbcGeneralOptionsRepository;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GeneralOptionsPersistenceAdapter implements GeneralOptionsRepositoryPort {
    private final R2dbcGeneralOptionsRepository globalRepository;
    private final R2dbcAgencySettingsRepository localRepository;
    private final GeneralOptionsMapper mapper;

    // -- GLOBAL --
    @Override
    public Mono<AppBusinessSettings> findByOrganizationId(UUID organizationId) {
        return globalRepository.findByOrganizationId(organizationId)
                .map(mapper::toDomainGlobal);
    }

    @Override
    public Mono<AppBusinessSettings> saveGlobal(AppBusinessSettings settings) {
        return globalRepository.findByOrganizationId(settings.getOrganizationId())
                .defaultIfEmpty(new AppBusinessSettingsEntity()) // Create new
                .flatMap(existing -> {
                    AppBusinessSettingsEntity entity = mapper.toEntityGlobal(settings);
                    // Preserve ID if exists
                    if (existing.getId() != null)
                        entity.setId(existing.getId());
                    else
                        entity.setId(null);

                    if (entity.getCreatedAt() == null)
                        entity.setCreatedAt(Instant.now());
                    entity.setUpdatedAt(Instant.now());

                    return globalRepository.save(entity);
                })
                .map(mapper::toDomainGlobal);
    }

    // -- LOCAL --
    @Override
    public Mono<AgencySettings> findByAgencyId(UUID agencyId) {
        return localRepository.findByAgencyId(agencyId)
                .map(mapper::toDomainLocal);
    }

    @Override
    public Mono<AgencySettings> saveLocal(AgencySettings settings) {
        return localRepository.findByAgencyId(settings.getAgencyId())
                .defaultIfEmpty(new AgencySettingsEntity())
                .flatMap(existing -> {
                    AgencySettingsEntity entity = mapper.toEntityLocal(settings);
                    if (existing.getId() != null)
                        entity.setId(existing.getId());
                    else
                        entity.setId(null);

                    entity.setUpdatedAt(Instant.now());

                    return localRepository.save(entity);
                })
                .map(mapper::toDomainLocal);
    }
}
