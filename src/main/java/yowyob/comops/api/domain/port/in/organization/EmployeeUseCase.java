package yowyob.comops.api.domain.port.in.organization;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.OrganizationMember;
import yowyob.comops.api.domain.model.security.Role;
import java.util.List;
import java.util.UUID;

public interface EmployeeUseCase {

    Mono<OrganizationMember> createEmployee(CreateEmployeeCommand command);

    Mono<Void> removeEmployee(UUID memberId);

    Flux<OrganizationMember> getEmployees(UUID organizationId);

    Mono<OrganizationMember> findMemberById(UUID memberId); // NOUVELLE MÉTHODE

    // -- Gestion des Rôles (RBAC) --
    Flux<Role> getAllRoles();

    Mono<Role> createRole(String name, String description);

    record CreateEmployeeCommand(
            UUID organizationId,
            String firstName,
            String lastName,
            String email,
            String password,
            UUID roleId,
            UUID agencyId,
            List<UUID> permissionIds) {
    }
}