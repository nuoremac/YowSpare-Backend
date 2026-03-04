package yowyob.comops.api.infrastructure.adapter.in.web.organization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.OrganizationMember;
import yowyob.comops.api.domain.model.security.Role;
import yowyob.comops.api.domain.port.in.organization.EmployeeUseCase;
import yowyob.comops.api.infrastructure.config.exception.ErrorResponse;
import yowyob.comops.api.infrastructure.config.security.SecurityUtils;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Tag(name = "Employees & Roles", description = "Gestion des membres de l'organisation et de leurs rôles")
public class EmployeeController {
    private final EmployeeUseCase employeeService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @Operation(summary = "Lister les employés", description = "Récupère la liste des employés de l'organisation courante. La visibilité dépend des droits de l'utilisateur (Owner/Admin voit tout, Manager voit son agence).")
    public Flux<OrganizationMember> getEmployees() {
        return securityUtils.getCurrentOrganizationId()
                .flatMapMany(employeeService::getEmployees);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un employé par son ID de membre")
    @ApiResponse(responseCode = "200", description = "Détails de l'employé", content = @Content(schema = @Schema(implementation = OrganizationMember.class)))
    @ApiResponse(responseCode = "404", description = "Employé non trouvé", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<ResponseEntity<OrganizationMember>> getEmployeeById(@PathVariable UUID id) {
        // La logique de sécurité est implicitement dans le service :
        // Si on essaie d'accéder à un membre hors de son scope, le service renverra un
        // Mono vide.
        return employeeService.findMemberById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer un nouvel employé", description = "Crée un nouveau compte utilisateur et l'ajoute comme membre à l'organisation. Nécessite des droits RH.")
    @ApiResponse(responseCode = "201", description = "Employé créé avec succès")
    public Mono<ResponseEntity<OrganizationMember>> createEmployee(@RequestBody CreateEmployeeRequest request) {
        return securityUtils.getCurrentOrganizationId()
                .flatMap(orgId -> {
                    EmployeeUseCase.CreateEmployeeCommand command = new EmployeeUseCase.CreateEmployeeCommand(
                            orgId,
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPassword(),
                            request.getRoleId(),
                            request.getAgencyId(),
                            request.getPermissionIds());
                    return employeeService.createEmployee(command);
                })
                .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(m));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un employé", description = "Supprime le lien membre et le compte utilisateur associé. Nécessite des droits RH.")
    public Mono<ResponseEntity<Void>> removeEmployee(@PathVariable UUID id) {
        return employeeService.removeEmployee(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @GetMapping("/roles")
    @Operation(summary = "Lister tous les rôles", description = "Récupère la liste de tous les rôles disponibles dans le système.")
    public Flux<Role> getRoles() {
        return employeeService.getAllRoles();
    }

    @PostMapping("/roles")
    @Operation(summary = "Créer un nouveau rôle global", description = "Crée un nouveau rôle réutilisable. Seul le propriétaire de l'organisation peut le faire.")
    public Mono<ResponseEntity<Role>> createRole(@RequestBody CreateRoleRequest request) {
        return employeeService.createRole(request.getName(), request.getDescription())
                .map(ResponseEntity::ok);
    }

    @Data
    @Schema(description = "Données requises pour créer un nouvel employé")
    public static class CreateEmployeeRequest {
        @Schema(example = "Marie")
        private String firstName;
        @Schema(example = "Durand")
        private String lastName;
        @Schema(example = "marie.durand@yowyob.com")
        private String email;
        @Schema(example = "Password456!", description = "Mot de passe initial défini par le RH")
        private String password;
        @Schema(description = "ID du rôle à assigner")
        private UUID roleId;
        @Schema(description = "ID de l'agence de rattachement (optionnel)")
        private UUID agencyId;
        @Schema(description = "Liste d'ID de permissions pour créer un rôle personnalisé à la volée (optionnel)")
        private List<UUID> permissionIds;
    }

    @Data
    @Schema(description = "Données pour créer un nouveau rôle")
    public static class CreateRoleRequest {
        @Schema(example = "Logistic Staff")
        private String name;
        @Schema(example = "Gère les opérations d'entrepôt et de livraison.")
        private String description;
    }
}