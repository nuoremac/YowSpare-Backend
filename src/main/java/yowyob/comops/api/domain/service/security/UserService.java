package yowyob.comops.api.domain.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.security.User;
import yowyob.comops.api.domain.port.in.security.UserUseCase;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcUserRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.mapper.security.UserMapper;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {
    private final R2dbcUserRepository r2dbcUserRepository;
    private final UserMapper userMapper;

    @Override
    public Mono<User> getUserById(UUID id) {
        return r2dbcUserRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public Mono<User> updateUserPlan(UUID userId, User.UserPlan plan) {
        return r2dbcUserRepository.findById(userId)
                .flatMap(entity -> {
                    entity.setPlan(plan.name());
                    return r2dbcUserRepository.save(entity);
                })
                .map(userMapper::toDomain);
    }

    @Override
    public Mono<User> updateOnboardingStep(UUID userId, int step, User.OnboardingStatus status) {
        return r2dbcUserRepository.findById(userId)
                .flatMap(entity -> {
                    entity.setOnboardingStep(step);
                    if (status != null) {
                        entity.setOnboardingStatus(status.name());
                    }
                    return r2dbcUserRepository.save(entity);
                })
                .map(userMapper::toDomain);
    }
}