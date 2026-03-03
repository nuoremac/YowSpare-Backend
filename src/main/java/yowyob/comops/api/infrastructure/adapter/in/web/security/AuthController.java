package yowyob.comops.api.infrastructure.adapter.in.web.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.security.User;
import yowyob.comops.api.domain.port.in.security.AuthUseCase;
import yowyob.comops.api.infrastructure.config.exception.ErrorResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints pour l'inscription et la connexion")
public class AuthController {

    private final AuthUseCase authService;

    @PostMapping("/login")
    @Operation(summary = "Connexion d'un utilisateur", description = "Authentifie un utilisateur avec son email et mot de passe, et retourne un token JWT ainsi que le profil utilisateur.")
    @ApiResponse(responseCode = "200", description = "Connexion réussie", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthUseCase.AuthResponse.class)))
    @ApiResponse(responseCode = "401", description = "Identifiants invalides", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<ResponseEntity<AuthUseCase.AuthResponse>> login(@RequestBody AuthUseCase.AuthRequest request) {
        return authService.login(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur", description = "Crée un nouveau compte utilisateur 'orphelin' (non rattaché à une organisation).")
    @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "409", description = "L'email est déjà utilisé pour un compte en attente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<ResponseEntity<User>> register(@RequestBody AuthUseCase.RegisterRequest request) {
        return authService.register(request)
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(user));
    }
}