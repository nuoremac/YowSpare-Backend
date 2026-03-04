package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.organization;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization.BusinessActorEntity;
import java.util.UUID;

@Repository
public interface R2dbcBusinessActorRepository extends ReactiveCrudRepository<BusinessActorEntity, UUID> {
}