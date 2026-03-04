package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.config;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.config.AppBusinessSettingsEntity; // Dummy type pour l'interface
import java.util.UUID;

// On utilise ReactiveCrudRepository sur une entité Dummy car on fait du SQL natif,
// mais pour être propre on peut créer une entité SequenceEntity.
// Ici on simplifie en utilisant une interface custom dans l'adapter directement ou via @Query.
@Repository
public interface R2dbcSequenceRepository extends ReactiveCrudRepository<AppBusinessSettingsEntity, UUID> {
    @Query("""
            INSERT INTO document_sequences (organization_id, document_type, year, current_value)
            VALUES (:organizationId, :documentType, :year, 1)
            ON CONFLICT (organization_id, document_type, year)
            DO UPDATE SET current_value = document_sequences.current_value + 1
            RETURNING current_value
            """)
    Mono<Integer> getNextValue(UUID organizationId, String documentType, Integer year);
}
