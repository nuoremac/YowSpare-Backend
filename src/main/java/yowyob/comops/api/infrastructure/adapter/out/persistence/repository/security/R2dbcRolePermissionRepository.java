package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security.RolePermissionEntity;
import java.util.UUID;

@Repository
public interface R2dbcRolePermissionRepository extends ReactiveCrudRepository<RolePermissionEntity, UUID> {
}