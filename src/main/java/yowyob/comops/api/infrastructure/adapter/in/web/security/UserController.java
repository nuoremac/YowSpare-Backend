package yowyob.comops.api.infrastructure.adapter.in.web.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.security.User;
import yowyob.comops.api.domain.port.in.security.UserUseCase;
import yowyob.comops.api.infrastructure.config.security.SecurityUtils;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestion du profil de l'utilisateur connecté")
public class UserController {
    private final UserUseCase userService;
    private final SecurityUtils securityUtils;

    @GetMapping("/me")
    @Operation(summary = "Obtenir mon profil", description = "Récupère les informations complètes de l'utilisateur actuellement authentifié.")
    @ApiResponse(responseCode = "200", description = "Profil utilisateur")
    public Mono<ResponseEntity<User>> getMe() {
        return securityUtils.getCurrentUserId()
                .flatMap(userService::getUserById)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/me/plan")
    @Operation(summary = "Mettre à jour mon plan d'abonnement", description = "Permet à l'utilisateur de changer son plan (ex: passer de FREE_TIER à FREELANCE).")
    public Mono<ResponseEntity<User>> updateMyPlan(@RequestBody UpdatePlanRequest request) {
        return securityUtils.getCurrentUserId()
                .flatMap(userId -> userService.updateUserPlan(userId, User.UserPlan.valueOf(request.getPlan())))
                .map(ResponseEntity::ok);
    }

    @PutMapping("/me/onboarding")
    @Operation(summary = "Mettre à jour mon étape d'onboarding", description = "Met à jour l'étape et le statut du processus d'intégration.")
    public Mono<ResponseEntity<User>> updateOnboarding(@RequestBody UpdateOnboardingRequest request) {
        return securityUtils.getCurrentUserId()
                .flatMap(userId -> userService.updateOnboardingStep(
                        userId,
                        request.getStep(),
                        request.getStatus() != null ? User.OnboardingStatus.valueOf(request.getStatus()) : null))
                .map(ResponseEntity::ok);
    }

    @Data
    @Schema(description = "Données pour la mise à jour du plan utilisateur")
    static class UpdatePlanRequest {
        @Schema(description = "Le nouveau plan", example = "FREELANCE", requiredMode = Schema.RequiredMode.REQUIRED)
        private String plan;
    }

    @Data
    @Schema(description = "Données pour la mise à jour de l'onboarding")
    static class UpdateOnboardingRequest {
        @Schema(description = "Le numéro de l'étape atteinte", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        private int step;
        @Schema(description = "Le nouveau statut de l'onboarding", example = "IN_PROGRESS")
        private String status;
    }
}