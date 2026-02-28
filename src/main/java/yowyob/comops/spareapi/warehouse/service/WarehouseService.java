package yowyob.comops.spareapi.warehouse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.warehouse.dto.ProductLocationDto;
import yowyob.comops.spareapi.warehouse.dto.UpsertProductLocationRequest;
import yowyob.comops.spareapi.warehouse.dto.WarehouseLayoutDto;
import yowyob.comops.spareapi.warehouse.entity.ProductLocationEntity;
import yowyob.comops.spareapi.warehouse.entity.WarehouseLayoutEntity;
import yowyob.comops.spareapi.warehouse.repository.ProductLocationRepository;
import yowyob.comops.spareapi.warehouse.repository.WarehouseLayoutRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class WarehouseService {
    private final WarehouseLayoutRepository layoutRepository;
    private final ProductLocationRepository locationRepository;
    private final ObjectMapper objectMapper;

    public WarehouseService(WarehouseLayoutRepository layoutRepository,
                            ProductLocationRepository locationRepository,
                            ObjectMapper objectMapper) {
        this.layoutRepository = layoutRepository;
        this.locationRepository = locationRepository;
        this.objectMapper = objectMapper;
    }

    public Mono<WarehouseLayoutDto> getLayout(UUID tenantId, UUID agencyId) {
        return layoutRepository.findByTenantIdAndAgencyId(tenantId, agencyId)
                .flatMap(entity -> {
                    try {
                        JsonNode layout = entity.getLayoutJson() == null || entity.getLayoutJson().isBlank()
                                ? objectMapper.createObjectNode()
                                : objectMapper.readTree(entity.getLayoutJson());
                        return Mono.just(new WarehouseLayoutDto(entity.getAgencyId(), entity.getType(), entity.getWidth(),
                                entity.getHeight(), layout));
                    } catch (Exception e) {
                        return Mono.error(new IllegalStateException("Invalid layout JSON in database."));
                    }
                })
                .switchIfEmpty(Mono.defer(() -> {
                    JsonNode empty = objectMapper.createObjectNode();
                    return Mono.just(new WarehouseLayoutDto(agencyId, "GRID", 12, 8, empty));
                }));
    }

    public Mono<WarehouseLayoutDto> upsertLayout(UUID tenantId, UUID agencyId, WarehouseLayoutDto req) {
        Instant now = Instant.now();
        return layoutRepository.findByTenantIdAndAgencyId(tenantId, agencyId)
                .defaultIfEmpty(new WarehouseLayoutEntity())
                .flatMap(existing -> {
                    boolean isNew = existing.getId() == null;
                    String json;
                    try {
                        json = objectMapper.writeValueAsString(req.layout());
                    } catch (Exception e) {
                        return Mono.error(new IllegalArgumentException("Invalid layout payload."));
                    }
                    WarehouseLayoutEntity next = WarehouseLayoutEntity.builder()
                            .id(existing.getId() != null ? existing.getId() : UUID.randomUUID())
                            .newEntity(isNew)
                            .tenantId(tenantId)
                            .agencyId(agencyId)
                            .type(req.type())
                            .width(req.width())
                            .height(req.height())
                            .layoutJson(json)
                            .createdAt(existing.getCreatedAt() != null ? existing.getCreatedAt() : now)
                            .updatedAt(now)
                            .build();
                    return layoutRepository.save(next);
                })
                .map(saved -> req);
    }

    public Flux<ProductLocationDto> listProductLocations(UUID tenantId, UUID agencyId) {
        return locationRepository.findAllByTenantIdAndAgencyId(tenantId, agencyId)
                .map(e -> ProductLocationDto.of(e.getAgencyId(), e.getProductId(), e.getBinCode(), e.getNote(), e.getUpdatedAt()));
    }

    public Flux<ProductLocationDto> listLocationsForProduct(UUID tenantId, UUID productId) {
        return locationRepository.findAllByTenantIdAndProductId(tenantId, productId)
                .map(e -> ProductLocationDto.of(e.getAgencyId(), e.getProductId(), e.getBinCode(), e.getNote(), e.getUpdatedAt()));
    }

    public Mono<ProductLocationDto> upsertProductLocation(UUID tenantId, UUID agencyId, UUID productId, UpsertProductLocationRequest req) {
        Instant now = Instant.now();
        String bin = req.binCode().trim();
        return locationRepository.findOne(tenantId, agencyId, productId)
                .defaultIfEmpty(new ProductLocationEntity())
                .flatMap(existing -> {
                    boolean isNew = existing.getId() == null;
                    ProductLocationEntity next = ProductLocationEntity.builder()
                            .id(existing.getId() != null ? existing.getId() : UUID.randomUUID())
                            .newEntity(isNew)
                            .tenantId(tenantId)
                            .agencyId(agencyId)
                            .productId(productId)
                            .binCode(bin)
                            .note(req.note())
                            .createdAt(existing.getCreatedAt() != null ? existing.getCreatedAt() : now)
                            .updatedAt(now)
                            .build();
                    return locationRepository.save(next);
                })
                .map(saved -> ProductLocationDto.of(saved.getAgencyId(), saved.getProductId(), saved.getBinCode(), saved.getNote(), saved.getUpdatedAt()));
    }
}
