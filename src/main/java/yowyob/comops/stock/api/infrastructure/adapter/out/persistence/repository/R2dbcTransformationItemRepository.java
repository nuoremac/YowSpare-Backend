package yowyob.comops.stock.api.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.infrastructure.adapter.out.persistence.entity.TransformationItemEntity;

import java.util.UUID;

@Repository
public interface R2dbcTransformationItemRepository extends ReactiveCrudRepository<TransformationItemEntity, UUID> {
    Flux<TransformationItemEntity> findByTransformationId(UUID transformationId);

    @Modifying
    @Query("DELETE FROM transformation_items WHERE transformation_id = :id")
    Mono<Void> deleteByTransformationId(UUID id);
}