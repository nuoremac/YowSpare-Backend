package yowyob.comops.api.domain.port.out.config;

import reactor.core.publisher.Mono;
import java.util.UUID;

public interface SequenceRepositoryPort {
    /**
     * Incrémente et retourne la prochaine valeur pour un type de document donné.
     * thread-safe et atomique.
     */
    Mono<Integer> getNextValue(UUID organizationId, String documentType, int year);
}