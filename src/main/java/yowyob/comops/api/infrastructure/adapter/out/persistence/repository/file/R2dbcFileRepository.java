package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.file;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.file.StoredFileEntity;
import java.util.UUID;

@Repository
public interface R2dbcFileRepository extends ReactiveCrudRepository<StoredFileEntity, UUID> {
}