package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security.RoleEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security.UserRoleEntity;
import java.util.UUID;

@Repository
public interface R2dbcUserRoleRepository extends ReactiveCrudRepository<UserRoleEntity, UUID> {
    @Query("SELECT r.* FROM roles r INNER JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = :userId")
    Flux<RoleEntity> findRolesByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM user_roles WHERE user_id = :userId")
    Mono<Void> deleteByUserId(UUID userId);
}