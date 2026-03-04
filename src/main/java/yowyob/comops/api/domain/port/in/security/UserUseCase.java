package yowyob.comops.api.domain.port.in.security;

import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.security.User;
import java.util.UUID;

public interface UserUseCase {
    Mono<User> getUserById(UUID id);

    Mono<User> updateUserPlan(UUID userId, User.UserPlan plan);

    Mono<User> updateOnboardingStep(UUID userId, int step, User.OnboardingStatus status);
}
