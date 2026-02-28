package yowyob.comops.spareapi.supplier.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.supplier.dto.*;
import yowyob.comops.spareapi.supplier.entity.SupplierEntity;
import yowyob.comops.spareapi.supplier.entity.SupplierProductEntity;
import yowyob.comops.spareapi.supplier.repository.SupplierProductRepository;
import yowyob.comops.spareapi.supplier.repository.SupplierRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierProductRepository supplierProductRepository;

    public SupplierService(SupplierRepository supplierRepository, SupplierProductRepository supplierProductRepository) {
        this.supplierRepository = supplierRepository;
        this.supplierProductRepository = supplierProductRepository;
    }

    public Flux<SupplierDto> search(UUID tenantId, String q, String status) {
        String normStatus = status == null ? null : status.trim().toUpperCase();
        return supplierRepository.search(tenantId, q == null || q.isBlank() ? null : q.trim(), normStatus)
                .map(SupplierService::toDto);
    }

    public Mono<SupplierDto> get(UUID tenantId, UUID id) {
        return supplierRepository.findByTenantAndId(tenantId, id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Supplier not found.")))
                .map(SupplierService::toDto);
    }

    public Mono<SupplierDto> upsert(UUID tenantId, UUID id, UpsertSupplierRequest req) {
        Instant now = Instant.now();
        String status = req.status() == null || req.status().isBlank() ? "ACTIVE" : req.status().trim().toUpperCase();
        Mono<SupplierEntity> existing = supplierRepository.findByTenantAndId(tenantId, id);
        return existing.defaultIfEmpty(new SupplierEntity())
                .flatMap(cur -> {
                    boolean isNew = cur.getId() == null;
                    SupplierEntity next = SupplierEntity.builder()
                            .id(cur.getId() != null ? cur.getId() : id)
                            .newEntity(isNew)
                            .tenantId(tenantId)
                            .name(req.name().trim())
                            .status(status)
                            .email(req.email())
                            .phone(req.phone())
                            .address(req.address())
                            .notes(req.notes())
                            .createdAt(cur.getCreatedAt() != null ? cur.getCreatedAt() : now)
                            .updatedAt(now)
                            .build();
                    return supplierRepository.save(next);
                })
                .map(SupplierService::toDto);
    }

    public Mono<Void> delete(UUID tenantId, UUID id) {
        return supplierRepository.findByTenantAndId(tenantId, id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Supplier not found.")))
                .flatMap(supplierRepository::delete);
    }

    public Flux<SupplierProductDto> listSupplierProducts(UUID tenantId, UUID supplierId) {
        return supplierProductRepository.listForSupplier(tenantId, supplierId).map(SupplierService::toDto);
    }

    public Flux<SupplierProductDto> listByProduct(UUID tenantId, UUID productId) {
        return supplierProductRepository.listForProduct(tenantId, productId).map(SupplierService::toDto);
    }

    public Mono<SupplierProductDto> upsertSupplierProduct(UUID tenantId, UUID supplierId, UUID productId, UpsertSupplierProductRequest req) {
        Instant now = Instant.now();
        return supplierProductRepository.findOne(tenantId, supplierId, productId)
                .defaultIfEmpty(new SupplierProductEntity())
                .flatMap(cur -> {
                    boolean isNew = cur.getId() == null;
                    SupplierProductEntity next = SupplierProductEntity.builder()
                            .id(cur.getId() != null ? cur.getId() : UUID.randomUUID())
                            .newEntity(isNew)
                            .tenantId(tenantId)
                            .supplierId(supplierId)
                            .productId(productId)
                            .leadTimeDays(req.leadTimeDays())
                            .moq(req.moq())
                            .preferred(req.preferred() != null && req.preferred())
                            .unitPrice(req.unitPrice())
                            .createdAt(cur.getCreatedAt() != null ? cur.getCreatedAt() : now)
                            .updatedAt(now)
                            .build();
                    return supplierProductRepository.save(next);
                })
                .map(SupplierService::toDto);
    }

    public Mono<Void> deleteSupplierProduct(UUID tenantId, UUID supplierId, UUID productId) {
        return supplierProductRepository.deleteOne(tenantId, supplierId, productId);
    }

    private static SupplierDto toDto(SupplierEntity e) {
        return new SupplierDto(
                e.getId(),
                e.getName(),
                e.getStatus(),
                e.getEmail(),
                e.getPhone(),
                e.getAddress(),
                e.getNotes(),
                e.getUpdatedAt()
        );
    }

    private static SupplierProductDto toDto(SupplierProductEntity e) {
        return new SupplierProductDto(
                e.getId(),
                e.getSupplierId(),
                e.getProductId(),
                e.getLeadTimeDays(),
                e.getMoq(),
                e.getPreferred(),
                e.getUnitPrice(),
                e.getUpdatedAt()
        );
    }
}
