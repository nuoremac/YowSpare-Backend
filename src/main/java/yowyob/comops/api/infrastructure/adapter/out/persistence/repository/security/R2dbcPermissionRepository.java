package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security.PermissionEntity;
import java.util.UUID;

@Repository
public interface R2dbcPermissionRepository extends ReactiveCrudRepository<PermissionEntity, UUID> {
    @Query("SELECT p.* FROM permissions p JOIN role_permissions rp ON p.id = rp.permission_id WHERE rp.role_id = :roleId")
    Flux<PermissionEntity> findByRoleId(UUID roleId);
}