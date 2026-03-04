package yowyob.comops.api.infrastructure.adapter.out.persistence.adapter.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.security.User;
import yowyob.comops.api.domain.port.out.security.UserRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security.RoleEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security.UserEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security.UserRoleEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.mapper.security.UserMapper;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcRoleRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcUserRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcUserRoleRepository;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {
    private final R2dbcUserRepository userRepository;
    private final R2dbcUserRoleRepository userRoleRepository;
    private final R2dbcRoleRepository roleRepository;
    private final UserMapper mapper;

    @Override
    public Mono<User> findById(UUID id) {
        return userRepository.findById(id)
                .flatMap(this::enrichUserWithRoles);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        // Retourne le premier trouvé (utilisation générique),
        // mais pour l'Auth précise on utilise findById ou l'AuthManager direct
        return userRepository.findByEmail(email).next()
                .flatMap(this::enrichUserWithRoles);
    }

    @Override
    public Mono<Boolean> existsByEmailAndOrganizationIdIsNull(String email) {
        return userRepository.existsByEmailAndOrganizationIdIsNull(email);
    }

    @Override
    public Mono<User> save(User user) {
        UserEntity entity = mapper.toEntity(user);

        if (entity.getId() == null)
            entity.setId(null);
        if (entity.getCreatedAt() == null)
            entity.setCreatedAt(Instant.now());

        // IMPORTANT : Le mapper doit gérer l'organizationId.
        // Si le User domain a organizationId null, l'entity aura null.

        return userRepository.save(entity)
                .flatMap(savedEntity -> {
                    if (user.getRoles() == null || user.getRoles().isEmpty()) {
                        return Mono.just(savedEntity);
                    }

                    // Gestion des rôles
                    return userRoleRepository.deleteByUserId(savedEntity.getId()) // Clean existant si update
                            .then(Flux.fromIterable(user.getRoles())
                                    .flatMap(roleName -> roleRepository.findByName(roleName))
                                    .flatMap(roleEntity -> {
                                        UserRoleEntity link = new UserRoleEntity(savedEntity.getId(),
                                                roleEntity.getId());
                                        return userRoleRepository.save(link);
                                    })
                                    .then(Mono.just(savedEntity)));
                })
                .flatMap(this::enrichUserWithRoles);
    }

    private Mono<User> enrichUserWithRoles(UserEntity entity) {
        User user = mapper.toDomain(entity);
        user.setId(entity.getId());

        // On récupère explicitement l'organizationId pour être sûr
        user.setOrganizationId(entity.getOrganizationId());

        return userRoleRepository.findRolesByUserId(entity.getId())
                .map(RoleEntity::getName)
                .collectList()
                .map(roleNames -> {
                    user.setRoles(roleNames);
                    return user;
                });
    }
}
