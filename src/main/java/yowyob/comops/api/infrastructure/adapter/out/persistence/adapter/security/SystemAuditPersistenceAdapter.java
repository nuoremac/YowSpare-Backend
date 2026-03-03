package yowyob.comops.api.infrastructure.adapter.out.persistence.adapter.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.security.SystemAudit;
import yowyob.comops.api.domain.port.out.security.SystemAuditRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security.SystemAuditEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcSystemAuditRepository;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SystemAuditPersistenceAdapter implements SystemAuditRepositoryPort {
    private final R2dbcSystemAuditRepository repository;

    @Override
    public Mono<Void> save(SystemAudit audit) {
        // Le service passe déjà le user (email), pas besoin de le re-fetch
        SystemAuditEntity entity = SystemAuditEntity.builder()
                .userEmail(audit.getUser())
                .action(audit.getAction())
                .details(audit.getRemarks())
                .createdAt(audit.getDate())
                .build();
        return repository.save(entity).then();
    }

    @Override
    public Flux<SystemAudit> findByUser(UUID userId, int limit) {
        return repository.findByUserId(userId, limit).map(this::mapToDomain);
    }

    @Override
    public Flux<SystemAudit> findByOrganization(UUID organizationId, int limit) {
        return repository.findByOrganizationId(organizationId, limit).map(this::mapToDomain);
    }

    private SystemAudit mapToDomain(SystemAuditEntity entity) {
        return SystemAudit.builder()
                .id(entity.getId())
                .user(entity.getUserEmail())
                .action(entity.getAction())
                .remarks(entity.getDetails())
                .date(entity.getCreatedAt())
                .build();
    }
}
