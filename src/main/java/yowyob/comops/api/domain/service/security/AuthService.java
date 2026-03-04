package yowyob.comops.api.domain.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.config.security.JwtUtil;
import yowyob.comops.api.domain.model.security.User;
import yowyob.comops.api.domain.port.in.security.AuthUseCase;
import yowyob.comops.api.domain.port.out.security.UserRepositoryPort;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<AuthResponse> login(AuthRequest request) {
        return authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()))
                .flatMap(auth -> {
                    // 1. Génération du Token (Sujet = UUID)
                    String token = jwtUtil.generateToken(auth);

                    // 2. Récupération PRÉCISE de l'utilisateur via son UUID unique
                    // Le "username" du principal est l'UUID de l'utilisateur (voir
                    // AuthenticationManager)
                    String userIdString = ((org.springframework.security.core.userdetails.User) auth.getPrincipal())
                            .getUsername();
                    UUID userId = UUID.fromString(userIdString);

                    // 3. On utilise findById pour récupérer l'objet User complet et fiable
                    return userRepository.findById(userId)
                            .map(user -> new AuthResponse(token, user));
                });
    }

    @Override
    public Mono<User> register(RegisterRequest request) {
        // Un nouvel utilisateur est "orphelin", son organizationId sera null.
        return userRepository.existsByEmailAndOrganizationIdIsNull(request.email())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono
                                .error(new IllegalArgumentException("An account is already pending for this email."));
                    }

                    User newUser = User.builder()
                            .id(null)
                            .organizationId(null) // Explicitement null à la création
                            .email(request.email())
                            .password(passwordEncoder.encode(request.password()))
                            .firstName(request.firstName())
                            .lastName(request.lastName())
                            .isActive(true)
                            .plan(User.UserPlan.FREE_TIER)
                            .onboardingStatus(User.OnboardingStatus.NOT_STARTED)
                            .onboardingStep(0)
                            .roles(List.of("ROLE_USER"))
                            .build();

                    return userRepository.save(newUser);
                });
    }
}