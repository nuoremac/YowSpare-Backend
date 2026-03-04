package yowyob.comops.api.domain.service.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.OrganizationMember;
import yowyob.comops.api.domain.model.security.Role;
import yowyob.comops.api.domain.model.security.User;
import yowyob.comops.api.domain.port.in.organization.EmployeeUseCase;
import yowyob.comops.api.domain.port.out.organization.EmployeeRepositoryPort;
import yowyob.comops.api.domain.port.out.security.UserRepositoryPort;
import yowyob.comops.api.infrastructure.config.security.RbacEvaluator;
import yowyob.comops.api.infrastructure.config.security.SecurityUtils;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcUserRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService implements EmployeeUseCase {
    private final EmployeeRepositoryPort employeeRepository;
    private final UserRepositoryPort userRepository;
    private final R2dbcUserRepository r2dbcUserRepository;
    private final RbacEvaluator rbacEvaluator;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Mono<OrganizationMember> createEmployee(CreateEmployeeCommand command) {
        return checkHrPermissions(command.organizationId(), command.agencyId())
                .then(Mono.defer(() -> r2dbcUserRepository
                        .existsByEmailAndOrganizationId(command.email(), command.organizationId())
                        .flatMap(exists -> {
                            if (Boolean.TRUE.equals(exists)) {
                                return Mono.error(new IllegalArgumentException(
                                        "User with this email already exists in this organization."));
                            }

                            return resolveRoleId(command)
                                    .flatMap(roleId -> {
                                        User newUser = User.builder()
                                                .organizationId(command.organizationId())
                                                .email(command.email())
                                                .password(passwordEncoder.encode(command.password()))
                                                .firstName(command.firstName())
                                                .lastName(command.lastName())
                                                .isActive(true)
                                                .plan(User.UserPlan.FREE_TIER)
                                                .onboardingStatus(User.OnboardingStatus.COMPLETED)
                                                .roles(List.of("ROLE_USER"))
                                                .build();

                                        return userRepository.save(newUser)
                                                .flatMap(savedUser -> {
                                                    OrganizationMember member = OrganizationMember.builder()
                                                            .organizationId(command.organizationId())
                                                            .userId(savedUser.getId())
                                                            .roleId(roleId)
                                                            .agencyId(command.agencyId())
                                                            .isActive(true)
                                                            .joinedAt(Instant.now())
                                                            .build();
                                                    return employeeRepository.saveMember(member);
                                                });
                                    });
                        })));
    }

    private Mono<Void> checkHrPermissions(UUID orgId, UUID targetAgencyId) {
        // CORRECTION MAJEURE ICI :
        // La méthode canManageStaffForAgency inclut déjà la vérification "isOwner".
        // Il suffit de l'appeler.
        return rbacEvaluator.canManageStaffForAgency(targetAgencyId)
                .flatMap(canManage -> {
                    if (Boolean.TRUE.equals(canManage)) {
                        return Mono.empty();
                    }
                    return Mono.error(
                            new IllegalStateException("Access Denied: You do not have HR permissions for this scope."));
                });
    }

    private Mono<UUID> resolveRoleId(CreateEmployeeCommand command) {
        if (command.permissionIds() != null && !command.permissionIds().isEmpty()) {
            String roleName = "CUSTOM_" + command.email().split("@")[0].toUpperCase() + "_"
                    + UUID.randomUUID().toString().substring(0, 4);
            return employeeRepository
                    .saveRole(Role.builder().name(roleName).description("Custom Role for " + command.email()).build())
                    .flatMap(savedRole -> employeeRepository
                            .assignPermissionsToRole(savedRole.getId(), command.permissionIds())
                            .thenReturn(savedRole.getId()));
        }
        return Mono.just(command.roleId());
    }

    @Override
    @Transactional
    public Mono<Void> removeEmployee(UUID memberId) {
        return employeeRepository.findMemberById(memberId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Member not found")))
                .flatMap(member -> checkHrPermissions(member.getOrganizationId(), member.getAgencyId())
                        .then(employeeRepository.deleteMember(memberId))
                        .then(r2dbcUserRepository.deleteById(member.getUserId())));
    }

    @Override
    public Flux<OrganizationMember> getEmployees(UUID organizationId) {
        return Mono.zip(rbacEvaluator.isOwner(), securityUtils.getCurrentUserId())
                .flatMapMany(tuple -> {
                    Boolean isOwner = tuple.getT1();
                    UUID userId = tuple.getT2();

                    if (Boolean.TRUE.equals(isOwner)) {
                        return employeeRepository.findByOrganizationId(organizationId);
                    }

                    return employeeRepository.findMemberByOrganizationIdAndUserId(organizationId, userId)
                            .flatMapMany(me -> {
                                if (me.getAgencyId() == null) {
                                    return employeeRepository.findByOrganizationId(organizationId);
                                } else {
                                    return employeeRepository.findByOrganizationIdAndAgencyId(organizationId,
                                            me.getAgencyId());
                                }
                            });
                });
    }

    @Override
    public Flux<Role> getAllRoles() {
        return employeeRepository.findAllRoles();
    }

    @Override
    public Mono<OrganizationMember> findMemberById(UUID memberId) {
        // On récupère le membre...
        return employeeRepository.findMemberById(memberId)
            // ...puis on vérifie si l'utilisateur courant a le droit de le voir.
            .flatMap(member -> 
                isOwnerOrHasScope(member.getOrganizationId(), member.getAgencyId())
                    .filter(Boolean::booleanValue) // Ne continue que si la permission est true
                    .map(canView -> member) // Si oui, on retourne le membre
            );
    }

    private Mono<Boolean> isOwnerOrHasScope(UUID organizationId, UUID targetAgencyId) {
        return Mono.zip(rbacEvaluator.isOwner(), securityUtils.getCurrentUserId())
            .flatMap(tuple -> {
                Boolean isOwner = tuple.getT1();
                if(Boolean.TRUE.equals(isOwner)) return Mono.just(true);

                UUID currentUserId = tuple.getT2();
                return employeeRepository.findMemberByOrganizationIdAndUserId(organizationId, currentUserId)
                    .map(currentUserMember -> {
                        // Un admin global voit tout
                        if (currentUserMember.getAgencyId() == null) return true;
                        // Un manager local ne voit que les membres de sa propre agence
                        return currentUserMember.getAgencyId().equals(targetAgencyId);
                    }).defaultIfEmpty(false);
            });
    }

    @Override
    public Mono<Role> createRole(String name, String description) {
        return rbacEvaluator.isOwner()
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new IllegalStateException("Only Owner can create global roles")))
                .flatMap(ok -> employeeRepository.saveRole(Role.builder().name(name).description(description).build()));
    }
}