package yowyob.comops.api.infrastructure.adapter.out.persistence.repository.thirdparty;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.infrastructure.adapter.out.persistence.entity.thirdparty.CustomerDetailsEntity;
import java.util.UUID;

@Repository
public interface R2dbcCustomerDetailsRepository extends ReactiveCrudRepository<CustomerDetailsEntity, UUID> {
}
