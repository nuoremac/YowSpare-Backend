package yowyob.comops.api.domain.port.out.security;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.security.SystemAudit;
import java.util.UUID;

public interface SystemAuditRepositoryPort {
    Mono<Void> save(SystemAudit audit);

    Flux<SystemAudit> findByUser(UUID userId, int limit);

    Flux<SystemAudit> findByOrganization(UUID organizationId, int limit);
}
