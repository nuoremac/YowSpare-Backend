package yowyob.comops.api.domain.port.out.organization;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.OrganizationMember;
import yowyob.comops.api.domain.model.security.Role;
import java.util.List;
import java.util.UUID;

public interface EmployeeRepositoryPort {
    // Membres
    Mono<OrganizationMember> saveMember(OrganizationMember member);

    Mono<Void> deleteMember(UUID id);

    // Recherches
    Flux<OrganizationMember> findByOrganizationId(UUID organizationId);

    Flux<OrganizationMember> findByOrganizationIdAndAgencyId(UUID organizationId, UUID agencyId);

    Mono<OrganizationMember> findMemberById(UUID id);

    Mono<OrganizationMember> findMemberByOrganizationIdAndUserId(UUID organizationId, UUID userId);

    Mono<Boolean> existsByOrganizationIdAndUserId(UUID organizationId, UUID userId);

    // Rôles
    Flux<Role> findAllRoles();

    Mono<Role> saveRole(Role role);

    Mono<Void> assignPermissionsToRole(UUID roleId, List<UUID> permissionIds);
}