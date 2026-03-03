package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security.RoleEntity;
import java.util.UUID;

@Repository
public interface R2dbcRoleRepository extends ReactiveCrudRepository<RoleEntity, UUID> {
    Mono<RoleEntity> findByName(String name);
}