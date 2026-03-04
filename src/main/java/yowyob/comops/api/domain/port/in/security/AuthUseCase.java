package yowyob.comops.api.domain.port.in.security;

import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.security.User;

public interface AuthUseCase {
    Mono<AuthResponse> login(AuthRequest request);

    Mono<User> register(RegisterRequest request);

    record AuthRequest(String email, String password) {
    }

    // La réponse contient l'objet User complet, incluant organizationId
    record AuthResponse(String token, User user) {
    }

    record RegisterRequest(
            String firstName,
            String lastName,
            String email,
            String company,
            String password) {
    }
}