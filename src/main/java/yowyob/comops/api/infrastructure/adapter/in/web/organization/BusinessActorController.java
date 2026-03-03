package yowyob.comops.api.infrastructure.adapter.in.web.organization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.BusinessActor;
import yowyob.comops.api.domain.port.in.organization.BusinessActorUseCase;
import yowyob.comops.api.infrastructure.config.exception.ErrorResponse;
import yowyob.comops.api.infrastructure.config.security.SecurityUtils;

@RestController
@RequestMapping("/actors")
@RequiredArgsConstructor
@Tag(name = "Business Actors", description = "Gestion du profil légal des propriétaires d'organisations")
public class BusinessActorController {
    private final BusinessActorUseCase actorService;
    private final SecurityUtils securityUtils;

    @PostMapping("/onboarding")
    @Operation(summary = "Créer son profil Business Actor", description = "Étape d'onboarding où l'utilisateur (futur propriétaire) enregistre ses informations légales. Prérequis à la création d'organisation.")
    @ApiResponse(responseCode = "200", description = "Profil Business Actor créé avec succès", content = @Content(schema = @Schema(implementation = BusinessActor.class)))
    @ApiResponse(responseCode = "400", description = "Utilisateur déjà lié à un Business Actor", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<ResponseEntity<BusinessActor>> onboardUser(@RequestBody BusinessActorRequest request) {
        BusinessActor actorDetails = BusinessActor.builder()
                .name(request.getName())
                .niu(request.getNiu())
                .tradeRegistryNumber(request.getTradeRegistryNumber())
                .contactPhone(request.getContactPhone())
                .businessAddress(request.getBusinessAddress())
                .website(request.getWebsite())
                .privateAddress(request.getPrivateAddress())
                .businessProfile(request.getBusinessProfile())
                .build();

        return securityUtils.getCurrentUserId()
                .flatMap(userId -> actorService.createActorFromUser(userId, actorDetails))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/me")
    @Operation(summary = "Obtenir son profil Business Actor", description = "Récupère les détails du profil Business Actor associé à l'utilisateur connecté.")
    @ApiResponse(responseCode = "200", description = "Profil Business Actor")
    @ApiResponse(responseCode = "404", description = "Aucun profil Business Actor trouvé pour cet utilisateur")
    public Mono<ResponseEntity<BusinessActor>> getMyProfile() {
        return securityUtils.getCurrentUserId()
                .flatMap(actorService::getCurrentActor)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    @Operation(summary = "Mettre à jour son profil Business Actor")
    @ApiResponse(responseCode = "200", description = "Profil mis à jour")
    public Mono<ResponseEntity<BusinessActor>> updateProfile(@RequestBody BusinessActorRequest details) {
        BusinessActor actorDetails = BusinessActor.builder()
                .name(details.getName())
                .contactPhone(details.getContactPhone())
                .businessAddress(details.getBusinessAddress())
                .website(details.getWebsite())
                .privateAddress(details.getPrivateAddress())
                .businessProfile(details.getBusinessProfile())
                .build();

        return securityUtils.getCurrentUser()
                .flatMap(user -> {
                    if (user.getBusinessActorId() == null)
                        return Mono.empty();
                    return actorService.updateActor(user.getBusinessActorId(), actorDetails);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Data
    @Schema(name = "BusinessActorRequest", description = "Données pour la création ou mise à jour d'un profil Business Actor")
    public static class BusinessActorRequest {
        @Schema(example = "Jean Dupont EI")
        private String name;
        @Schema(example = "M012345678901X")
        private String niu;
        @Schema(example = "RC/DLA/2025/A/123")
        private String tradeRegistryNumber;
        @Schema(example = "https://my-business.com")
        private String website;
        @Schema(example = "+237699887766")
        private String contactPhone;
        @Schema(example = "123 Rue de la Liberté, Bali")
        private String privateAddress;
        @Schema(example = "456 Avenue des Cocotiers, Akwa")
        private String businessAddress;
        @Schema(description = "Description de l'activité ou du profil")
        private String businessProfile;
    }
}