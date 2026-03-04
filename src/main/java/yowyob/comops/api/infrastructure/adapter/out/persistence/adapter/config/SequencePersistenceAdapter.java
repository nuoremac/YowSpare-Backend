package yowyob.comops.api.infrastructure.adapter.out.persistence.adapter.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.port.out.config.SequenceRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.config.R2dbcSequenceRepository;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SequencePersistenceAdapter implements SequenceRepositoryPort {
    private final R2dbcSequenceRepository repository;

    @Override
    public Mono<Integer> getNextValue(UUID organizationId, String documentType, int year) {
        return repository.getNextValue(organizationId, documentType, year);
    }
}