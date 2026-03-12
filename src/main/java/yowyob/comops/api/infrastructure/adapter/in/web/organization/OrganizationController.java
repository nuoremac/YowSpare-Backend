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
import yowyob.comops.api.domain.model.organization.Organization;
import yowyob.comops.api.domain.port.in.organization.OrganizationUseCase;
import yowyob.comops.api.infrastructure.config.exception.ErrorResponse;
import yowyob.comops.api.infrastructure.config.security.SecurityUtils;
import java.util.UUID;

@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
@Tag(name = "Organizations", description = "Gestion des entités organisationnelles")
public class OrganizationController {
    private final OrganizationUseCase organizationService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @Operation(summary = "Créer une nouvelle organisation", description = "Crée une nouvelle organisation pour l'utilisateur connecté, qui doit au préalable avoir créé son profil Business Actor.")
    @ApiResponse(responseCode = "201", description = "Organisation créée avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Organization.class)))
    @ApiResponse(responseCode = "400", description = "Requête invalide ou prérequis non rempli (ex: pas de Business Actor)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "Accès refusé (ex: le plan de l'utilisateur ne permet pas la création)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<ResponseEntity<Organization>> createOrganization(@RequestBody CreateOrganizationRequest request) {
        Organization organization = Organization.builder()
                .name(request.getName())
                .serviceType(request.getServiceType())
                .email(request.getEmail())
                .description(request.getDescription())
                .logoUri(request.getLogoUri())
                .logoId(request.getLogoId())
                .build();

        return securityUtils.getCurrentUser()
                .flatMap(user -> {
                    if (user.getBusinessActorId() == null) {
                        return Mono.error(new IllegalStateException(
                                "Business Actor profile required before creating an organization."));
                    }
                    return organizationService.createOrganization(organization, user.getBusinessActorId());
                })
                .map(org -> ResponseEntity.status(HttpStatus.CREATED).body(org));
    }

    @GetMapping("/my")
    @Operation(summary = "Lister mes organisations", description = "Récupère la liste des organisations dont l'utilisateur connecté est propriétaire.")
    public Flux<Organization> getMyOrganizations() {
        return securityUtils.getCurrentUser()
                .filter(u -> u.getBusinessActorId() != null)
                .flatMapMany(u -> organizationService.getMyOrganizations(u.getBusinessActorId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une organisation par son ID")
    @ApiResponse(responseCode = "200", description = "Détails de l'organisation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Organization.class)))
    @ApiResponse(responseCode = "404", description = "Organisation non trouvée", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<ResponseEntity<Organization>> getOrganizationById(@PathVariable UUID id) {
        return organizationService.getOrganizationById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre à jour une organisation (partiel)", description = "Met à jour les informations d'une organisation. Seul le propriétaire peut effectuer cette action.")
    public Mono<ResponseEntity<Organization>> updateOrganization(@PathVariable UUID id,
            @RequestBody CreateOrganizationRequest request) {
        Organization organization = Organization.builder()
                .name(request.getName())
                .serviceType(request.getServiceType())
                .email(request.getEmail())
                .description(request.getDescription())
                .logoUri(request.getLogoUri())
                .logoId(request.getLogoId())
                .build();

        return securityUtils.getCurrentUser()
                .flatMap(user -> organizationService.updateOrganization(id, organization, user.getBusinessActorId()))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{id}/transfer/{newOwnerId}")
    @Operation(summary = "Transférer la propriété d'une organisation", description = "Transfère la propriété d'une organisation à un autre Business Actor. Seul le propriétaire actuel peut initier le transfert.")
    public Mono<ResponseEntity<Organization>> transferOwnership(@PathVariable UUID id, @PathVariable UUID newOwnerId) {
        return securityUtils.getCurrentUser()
                .flatMap(user -> organizationService.transferOwnership(id, user.getBusinessActorId(), newOwnerId))
                .map(ResponseEntity::ok);
    }

    @Data
    @Schema(description = "Données requises pour créer ou mettre à jour une organisation")
    public static class CreateOrganizationRequest {
        @Schema(description = "Nom légal de l'organisation", example = "Yowyob Logistics SARL")
        private String name;
        @Schema(description = "Secteur d'activité", example = "Logistique et Transport")
        private String serviceType;
        @Schema(description = "Email de contact principal", example = "contact@yowlog.com")
        private String email;
        @Schema(description = "Description courte de l'activité")
        private String description;
        @Schema(description = "URL publique du logo de l'organisation", example = "/files/4de2dc31-25ab-4e2a-bf9d-11ac46014e8d")
        private String logoUri;
        @Schema(description = "Identifiant du fichier logo stocké", example = "4de2dc31-25ab-4e2a-bf9d-11ac46014e8d")
        private UUID logoId;
    }
}
