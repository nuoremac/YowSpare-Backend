package yowyob.comops.api.domain.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.security.SystemAudit;
import yowyob.comops.api.domain.port.out.security.SystemAuditRepositoryPort;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SystemAuditService {
    private final SystemAuditRepositoryPort repository;

    public Mono<Void> logAction(UUID userId, String userEmail, UUID organizationId, String action, String details) {
        SystemAudit audit = SystemAudit.builder()
                .user(userEmail)
                .action(action)
                .remarks(details)
                .date(Instant.now())
                // On pourrait aussi stocker userId et organizationId si le modèle domain les
                // avait
                .build();
        return repository.save(audit);
    }

    public Flux<SystemAudit> getUserActivity(UUID userId, int limit) {
        return repository.findByUser(userId, limit);
    }

    public Flux<SystemAudit> getOrganizationActivity(UUID organizationId, int limit) {
        return repository.findByOrganization(organizationId, limit);
    }
}