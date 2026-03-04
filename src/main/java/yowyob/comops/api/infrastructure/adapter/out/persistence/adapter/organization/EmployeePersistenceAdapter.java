package yowyob.comops.api.infrastructure.adapter.out.persistence.adapter.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.OrganizationMember;
import yowyob.comops.api.domain.model.security.Permission;
import yowyob.comops.api.domain.model.security.Role;
import yowyob.comops.api.domain.port.out.organization.EmployeeRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.AgencyEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.OrganizationMemberEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security.RoleEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security.RolePermissionEntity;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization.R2dbcAgencyRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization.R2dbcOrganizationMemberRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcPermissionRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcRolePermissionRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcRoleRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcUserRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmployeePersistenceAdapter implements EmployeeRepositoryPort {
    private final R2dbcOrganizationMemberRepository memberRepository;
    private final R2dbcUserRepository userRepository;
    private final R2dbcAgencyRepository agencyRepository;
    private final R2dbcRoleRepository roleRepository;
    private final R2dbcPermissionRepository permissionRepository;
    private final R2dbcRolePermissionRepository rolePermissionRepository;

    @Override
    public Mono<OrganizationMember> saveMember(OrganizationMember domain) {
        OrganizationMemberEntity entity = new OrganizationMemberEntity(
                domain.getId(),
                domain.getOrganizationId(),
                domain.getUserId(),
                domain.getAgencyId(),
                domain.getRoleId(),
                domain.isActive(),
                domain.getJoinedAt() != null ? domain.getJoinedAt() : Instant.now());

        if (entity.getId() == null)
            entity.setId(null);

        return memberRepository.save(entity)
                .flatMap(this::enrichMember);
    }

    @Override
    public Flux<OrganizationMember> findByOrganizationId(UUID organizationId) {
        return memberRepository.findByOrganizationId(organizationId)
                .flatMap(this::enrichMember);
    }

    @Override
    public Mono<OrganizationMember> findMemberById(UUID id) {
        return memberRepository.findById(id)
                .flatMap(this::enrichMember);
    }

    @Override
    public Mono<OrganizationMember> findMemberByOrganizationIdAndUserId(UUID organizationId, UUID userId) {
        return memberRepository.findByOrganizationIdAndUserId(organizationId, userId)
                .flatMap(this::enrichMember);
    }

    @Override
    public Flux<OrganizationMember> findByOrganizationIdAndAgencyId(UUID organizationId, UUID agencyId) {
        return memberRepository.findByOrganizationIdAndAgencyId(organizationId, agencyId)
                .flatMap(this::enrichMember);
    }

    @Override
    public Mono<Void> deleteMember(UUID id) {
        return memberRepository.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsByOrganizationIdAndUserId(UUID organizationId, UUID userId) {
        return memberRepository.existsByOrganizationIdAndUserId(organizationId, userId);
    }

    // -- Roles Logic --

    @Override
    public Flux<Role> findAllRoles() {
        return roleRepository.findAll()
                .flatMap(entity -> permissionRepository.findByRoleId(entity.getId())
                        .collectList()
                        .map(perms -> {
                            Role role = Role.builder()
                                    .id(entity.getId())
                                    .name(entity.getName())
                                    .description(entity.getDescription())
                                    .build();

                            role.setPermissions(perms.stream()
                                    .map(p -> new Permission(p.getId(), p.getResource(), p.getAction(),
                                            p.getDescription()))
                                    .toList());
                            return role;
                        }));
    }

    @Override
    public Mono<Role> saveRole(Role role) {
        RoleEntity entity = new RoleEntity(role.getId(), role.getName(), role.getDescription());
        if (entity.getId() == null)
            entity.setId(null);

        return roleRepository.save(entity)
                .map(e -> Role.builder().id(e.getId()).name(e.getName()).description(e.getDescription()).build());
    }

    @Override
    public Mono<Void> assignPermissionsToRole(UUID roleId, List<UUID> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty())
            return Mono.empty();

        List<RolePermissionEntity> links = permissionIds.stream()
                .map(pId -> new RolePermissionEntity(roleId, pId))
                .collect(Collectors.toList());

        return rolePermissionRepository.saveAll(links).then();
    }

    // Helper d'enrichissement (JOIN manuel)
    private Mono<OrganizationMember> enrichMember(OrganizationMemberEntity entity) {
        return Mono.zip(
                userRepository.findById(entity.getUserId()), // User info
                roleRepository.findById(entity.getRoleId()), // Role info
                entity.getAgencyId() != null ? agencyRepository.findById(entity.getAgencyId())
                        : Mono.just(new AgencyEntity()) // Agency info
        ).map(tuple -> OrganizationMember.builder()
                .id(entity.getId())
                .organizationId(entity.getOrganizationId())
                .userId(entity.getUserId())
                .userEmail(tuple.getT1().getEmail())
                .userFirstName(tuple.getT1().getFirstName())
                .userLastName(tuple.getT1().getLastName())
                .roleId(entity.getRoleId())
                .roleName(tuple.getT2().getName())
                .agencyId(entity.getAgencyId())
                .agencyName(tuple.getT3().getName()) // Sera null si AgencyEntity est vide (new)
                .isActive(entity.isActive())
                .joinedAt(entity.getJoinedAt())
                .build());
    }
}