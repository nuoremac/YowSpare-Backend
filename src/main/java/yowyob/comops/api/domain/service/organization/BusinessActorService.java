package yowyob.comops.api.domain.service.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.BusinessActor;
import yowyob.comops.api.domain.port.in.organization.BusinessActorUseCase;
import yowyob.comops.api.domain.port.out.organization.BusinessActorRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcUserRepository;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusinessActorService implements BusinessActorUseCase {
    private final BusinessActorRepositoryPort actorRepository;
    private final R2dbcUserRepository userRepository;

    @Override
    @Transactional
    public Mono<BusinessActor> createActorFromUser(UUID userId, BusinessActor actorDetails) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> {
                    if (user.getBusinessActorId() != null) {
                        return Mono.error(new IllegalStateException("User is already a Business Actor"));
                    }

                    actorDetails.setId(null);
                    actorDetails.setCreatedAt(Instant.now());

                    return actorRepository.save(actorDetails)
                            .flatMap(savedActor -> {
                                user.setBusinessActorId(savedActor.getId());
                                return userRepository.save(user).thenReturn(savedActor);
                            });
                });
    }

    @Override
    public Mono<BusinessActor> getCurrentActor(UUID userId) {
        return userRepository.findById(userId)
                .flatMap(user -> {
                    if (user.getBusinessActorId() == null)
                        return Mono.empty();
                    return actorRepository.findById(user.getBusinessActorId());
                });
    }

    @Override
    public Mono<BusinessActor> updateActor(UUID actorId, BusinessActor details) {
        return actorRepository.findById(actorId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Actor not found")))
                .flatMap(existing -> {
                    if (details.getName() != null)
                        existing.setName(details.getName());
                    if (details.getContactPhone() != null)
                        existing.setContactPhone(details.getContactPhone());
                    if (details.getBusinessAddress() != null)
                        existing.setBusinessAddress(details.getBusinessAddress());

                    return actorRepository.save(existing);
                });
    }
}