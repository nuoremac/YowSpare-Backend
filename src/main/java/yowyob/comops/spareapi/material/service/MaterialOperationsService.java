package yowyob.comops.spareapi.material.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.material.dto.*;
import yowyob.comops.spareapi.material.entity.DepartmentMemberEntity;
import yowyob.comops.spareapi.material.entity.DepartmentEntity;
import yowyob.comops.spareapi.material.entity.MaterialRequestEntity;
import yowyob.comops.spareapi.material.entity.MaterialRequestItemEntity;
import yowyob.comops.spareapi.material.entity.TraceEventEntity;
import yowyob.comops.spareapi.material.repository.DepartmentMemberRepository;
import yowyob.comops.spareapi.material.repository.DepartmentRepository;
import yowyob.comops.spareapi.material.repository.MaterialRequestItemRepository;
import yowyob.comops.spareapi.material.repository.MaterialRequestRepository;
import yowyob.comops.spareapi.material.repository.TraceEventRepository;

import java.time.Instant;
import java.util.*;

@Service
public class MaterialOperationsService {
    private static final String ENTITY_TYPE_REQUEST = "MATERIAL_REQUEST";

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_PARTIALLY_ISSUED = "PARTIALLY_ISSUED";
    private static final String STATUS_ISSUED = "ISSUED";
    private static final String STATUS_PARTIALLY_RETURNED = "PARTIALLY_RETURNED";
    private static final String STATUS_CLOSED = "CLOSED";
    private static final String STATUS_REJECTED = "REJECTED";

    private final DepartmentRepository departmentRepository;
    private final DepartmentMemberRepository departmentMemberRepository;
    private final MaterialRequestRepository requestRepository;
    private final MaterialRequestItemRepository itemRepository;
    private final TraceEventRepository traceEventRepository;
    private final ObjectMapper objectMapper;

    public MaterialOperationsService(DepartmentRepository departmentRepository,
                                     DepartmentMemberRepository departmentMemberRepository,
                                     MaterialRequestRepository requestRepository,
                                     MaterialRequestItemRepository itemRepository,
                                     TraceEventRepository traceEventRepository,
                                     ObjectMapper objectMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMemberRepository = departmentMemberRepository;
        this.requestRepository = requestRepository;
        this.itemRepository = itemRepository;
        this.traceEventRepository = traceEventRepository;
        this.objectMapper = objectMapper;
    }

    public Flux<DepartmentDto> listDepartments(UUID tenantId, UUID agencyId, Boolean active) {
        return departmentRepository.search(tenantId, agencyId, active)
                .map(MaterialOperationsService::toDto);
    }

    public Mono<DepartmentDto> createDepartment(UUID tenantId, CreateDepartmentRequest req) {
        String code = normalizeCode(req.code());
        Instant now = Instant.now();
        return departmentRepository.findByTenantAgencyAndCode(tenantId, req.agencyId(), code)
                .flatMap(existing -> Mono.<DepartmentEntity>error(new IllegalArgumentException("Department code already exists in this agency.")))
                .switchIfEmpty(Mono.defer(() -> {
                    DepartmentEntity entity = DepartmentEntity.builder()
                            .id(UUID.randomUUID())
                            .newEntity(true)
                            .tenantId(tenantId)
                            .agencyId(req.agencyId())
                            .code(code)
                            .name(req.name().trim())
                            .active(req.active() == null || req.active())
                            .createdAt(now)
                            .updatedAt(now)
                            .build();
                    return departmentRepository.save(entity);
                }))
                .map(MaterialOperationsService::toDto);
    }

    public Mono<DepartmentDto> updateDepartment(UUID tenantId, UUID id, UpdateDepartmentRequest req) {
        Instant now = Instant.now();
        return departmentRepository.findByTenantAndId(tenantId, id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Department not found.")))
                .flatMap(cur -> {
                    String name = cur.getName();
                    if (req.name() != null && !req.name().isBlank()) {
                        name = req.name().trim();
                    }
                    Boolean active = req.active() == null ? cur.getActive() : req.active();
                    DepartmentEntity next = DepartmentEntity.builder()
                            .id(cur.getId())
                            .newEntity(false)
                            .tenantId(cur.getTenantId())
                            .agencyId(cur.getAgencyId())
                            .code(cur.getCode())
                            .name(name)
                            .active(active)
                            .createdAt(cur.getCreatedAt())
                            .updatedAt(now)
                            .build();
                    return departmentRepository.save(next);
                })
                .map(MaterialOperationsService::toDto);
    }

    public Mono<Void> deleteDepartment(UUID tenantId, UUID id) {
        Instant now = Instant.now();
        return departmentRepository.findByTenantAndId(tenantId, id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Department not found.")))
                .flatMap(cur -> {
                    if (Boolean.FALSE.equals(cur.getActive())) {
                        return Mono.empty();
                    }
                    DepartmentEntity next = DepartmentEntity.builder()
                            .id(cur.getId())
                            .newEntity(false)
                            .tenantId(cur.getTenantId())
                            .agencyId(cur.getAgencyId())
                            .code(cur.getCode())
                            .name(cur.getName())
                            .active(false)
                            .createdAt(cur.getCreatedAt())
                            .updatedAt(now)
                            .build();
                    return departmentRepository.save(next).then();
                });
    }

    public Flux<DepartmentMemberDto> listDepartmentMembers(UUID tenantId, UUID departmentId) {
        return departmentRepository.findByTenantAndId(tenantId, departmentId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Department not found.")))
                .flatMapMany(ignore -> departmentMemberRepository.findByTenantAndDepartment(tenantId, departmentId))
                .map(MaterialOperationsService::toDto);
    }

    public Mono<DepartmentMemberDto> addDepartmentMember(UUID tenantId, UUID departmentId, CreateDepartmentMemberRequest req) {
        String userId = normalizeUserId(req.userId());
        Instant now = Instant.now();
        return departmentRepository.findByTenantAndId(tenantId, departmentId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Department not found.")))
                .flatMap(ignore -> departmentMemberRepository.findByTenantDepartmentAndUserId(tenantId, departmentId, userId)
                        .switchIfEmpty(Mono.defer(() -> departmentMemberRepository.save(
                                DepartmentMemberEntity.builder()
                                        .id(UUID.randomUUID())
                                        .newEntity(true)
                                        .tenantId(tenantId)
                                        .departmentId(departmentId)
                                        .userId(userId)
                                        .createdAt(now)
                                        .updatedAt(now)
                                        .build()
                        ))))
                .map(MaterialOperationsService::toDto);
    }

    public Mono<Void> removeDepartmentMember(UUID tenantId, UUID departmentId, String userIdRaw) {
        String userId = normalizeUserId(userIdRaw);
        return departmentMemberRepository.findByTenantDepartmentAndUserId(tenantId, departmentId, userId)
                .flatMap(departmentMemberRepository::delete)
                .then();
    }

    public Flux<MaterialRequestDto> listRequests(UUID tenantId, UUID agencyId, UUID departmentId, String status) {
        String normalizedStatus = normalizeStatus(status);
        return requestRepository.search(tenantId, agencyId, departmentId, normalizedStatus)
                .flatMap(req -> toRequestDto(tenantId, req));
    }

    public Mono<MaterialRequestDto> getRequest(UUID tenantId, UUID id) {
        return loadRequest(tenantId, id).flatMap(req -> toRequestDto(tenantId, req));
    }

    public Mono<MaterialRequestDto> createRequest(UUID tenantId, String actorId, CreateMaterialRequestRequest req) {
        Map<UUID, MaterialRequestItemInput> items = validateAndIndexCreateItems(req.items());
        return departmentRepository.findByTenantAndId(tenantId, req.departmentId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Department not found.")))
                .flatMap(department -> {
                    if (Boolean.FALSE.equals(department.getActive())) {
                        return Mono.error(new IllegalStateException("Department is inactive."));
                    }

                    Instant now = Instant.now();
                    MaterialRequestEntity request = MaterialRequestEntity.builder()
                            .id(UUID.randomUUID())
                            .newEntity(true)
                            .tenantId(tenantId)
                            .agencyId(department.getAgencyId())
                            .departmentId(department.getId())
                            .status(STATUS_PENDING)
                            .reasonCode(normalizeReasonCode(req.reasonCode()))
                            .reasonText(req.reasonText())
                            .requestedBy(actorId)
                            .expectedReturnAt(req.expectedReturnAt())
                            .createdAt(now)
                            .updatedAt(now)
                            .build();

                    return requestRepository.save(request)
                            .flatMap(saved -> saveRequestItems(tenantId, saved.getId(), items, now)
                                    .then(appendTrace(
                                            tenantId,
                                            ENTITY_TYPE_REQUEST,
                                            saved.getId(),
                                            "REQUEST_CREATED",
                                            actorId,
                                            saved.getAgencyId(),
                                            saved.getDepartmentId(),
                                            Map.of(
                                                    "reasonCode", Objects.toString(saved.getReasonCode(), ""),
                                                    "itemsCount", items.size()
                                            )
                                    ))
                                    .then(getRequest(tenantId, saved.getId())));
                });
    }

    public Mono<MaterialRequestDto> approveRequest(UUID tenantId, UUID id, String actorId) {
        Instant now = Instant.now();
        return loadRequest(tenantId, id)
                .flatMap(req -> {
                    if (!STATUS_PENDING.equals(req.getStatus())) {
                        return Mono.error(new IllegalStateException("Only pending requests can be approved."));
                    }
                    req.setStatus(STATUS_APPROVED);
                    req.setApprovedBy(actorId);
                    req.setApprovedAt(now);
                    req.setUpdatedAt(now);
                    return requestRepository.save(req);
                })
                .flatMap(saved -> appendTrace(
                        tenantId,
                        ENTITY_TYPE_REQUEST,
                        saved.getId(),
                        "REQUEST_APPROVED",
                        actorId,
                        saved.getAgencyId(),
                        saved.getDepartmentId(),
                        null
                ).then(getRequest(tenantId, saved.getId())));
    }

    public Mono<MaterialRequestDto> rejectRequest(UUID tenantId, UUID id, String actorId, RejectMaterialRequestRequest req) {
        Instant now = Instant.now();
        return loadRequest(tenantId, id)
                .flatMap(current -> {
                    if (STATUS_ISSUED.equals(current.getStatus()) || STATUS_PARTIALLY_RETURNED.equals(current.getStatus())
                            || STATUS_CLOSED.equals(current.getStatus())) {
                        return Mono.error(new IllegalStateException("Issued or closed requests cannot be rejected."));
                    }
                    current.setStatus(STATUS_REJECTED);
                    current.setClosedBy(actorId);
                    current.setClosedAt(now);
                    current.setCloseReason(req == null ? null : req.reason());
                    current.setUpdatedAt(now);
                    return requestRepository.save(current);
                })
                .flatMap(saved -> appendTrace(
                        tenantId,
                        ENTITY_TYPE_REQUEST,
                        saved.getId(),
                        "REQUEST_REJECTED",
                        actorId,
                        saved.getAgencyId(),
                        saved.getDepartmentId(),
                        req == null ? null : Map.of("reason", Objects.toString(req.reason(), ""))
                ).then(getRequest(tenantId, saved.getId())));
    }

    public Mono<MaterialRequestDto> issue(UUID tenantId, UUID id, String actorId, MaterialActionRequest req) {
        if (req == null || req.items() == null || req.items().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Issue items are required."));
        }

        Map<UUID, MaterialQuantityUpdate> updates = validateAndIndexUpdates(req.items());
        Instant now = Instant.now();

        return loadRequest(tenantId, id)
                .flatMap(request -> {
                    if (!(STATUS_APPROVED.equals(request.getStatus())
                            || STATUS_PARTIALLY_ISSUED.equals(request.getStatus())
                            || STATUS_ISSUED.equals(request.getStatus()))) {
                        return Mono.error(new IllegalStateException("Only approved requests can be issued."));
                    }
                    return itemRepository.findByTenantAndRequestId(tenantId, id)
                            .collectList()
                            .flatMap(items -> {
                                applyIssueUpdates(items, updates);

                                boolean allIssued = items.stream().allMatch(this::isFullyIssued);
                                boolean anyIssued = items.stream().anyMatch(i -> safe(i.getQuantityIssued()) > 0);
                                if (allIssued) {
                                    request.setStatus(STATUS_ISSUED);
                                } else if (anyIssued) {
                                    request.setStatus(STATUS_PARTIALLY_ISSUED);
                                }
                                request.setIssuedBy(actorId);
                                request.setIssuedAt(now);
                                request.setUpdatedAt(now);

                                return requestRepository.save(request)
                                        .flatMap(savedReq -> itemRepository.saveAll(markExisting(items, now)).collectList()
                                                .then(appendTrace(
                                                        tenantId,
                                                        ENTITY_TYPE_REQUEST,
                                                        savedReq.getId(),
                                                        "MATERIAL_ISSUED",
                                                        actorId,
                                                        savedReq.getAgencyId(),
                                                        savedReq.getDepartmentId(),
                                                        Map.of("note", Objects.toString(req.note(), ""), "items", updates.size())
                                                ))
                                                .then(getRequest(tenantId, savedReq.getId())));
                            });
                });
    }

    public Mono<MaterialRequestDto> registerReturn(UUID tenantId, UUID id, String actorId, MaterialActionRequest req) {
        if (req == null || req.items() == null || req.items().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Return items are required."));
        }

        Map<UUID, MaterialQuantityUpdate> updates = validateAndIndexUpdates(req.items());
        Instant now = Instant.now();

        return loadRequest(tenantId, id)
                .flatMap(request -> {
                    if (!(STATUS_ISSUED.equals(request.getStatus())
                            || STATUS_PARTIALLY_RETURNED.equals(request.getStatus())
                            || STATUS_PARTIALLY_ISSUED.equals(request.getStatus()))) {
                        return Mono.error(new IllegalStateException("Only issued requests can be returned."));
                    }

                    return itemRepository.findByTenantAndRequestId(tenantId, id)
                            .collectList()
                            .flatMap(items -> {
                                applyReturnUpdates(items, updates);

                                int totalIssued = items.stream().mapToInt(i -> safe(i.getQuantityIssued())).sum();
                                int totalReturned = items.stream().mapToInt(i -> safe(i.getQuantityReturned())).sum();
                                if (totalIssued <= 0) {
                                    return Mono.error(new IllegalStateException("Nothing has been issued for this request."));
                                }

                                boolean allReturned = totalReturned >= totalIssued;
                                if (allReturned) {
                                    request.setStatus(STATUS_CLOSED);
                                    request.setClosedBy(actorId);
                                    request.setClosedAt(now);
                                } else {
                                    request.setStatus(STATUS_PARTIALLY_RETURNED);
                                }
                                request.setUpdatedAt(now);

                                return requestRepository.save(request)
                                        .flatMap(savedReq -> itemRepository.saveAll(markExisting(items, now)).collectList()
                                                .then(appendTrace(
                                                        tenantId,
                                                        ENTITY_TYPE_REQUEST,
                                                        savedReq.getId(),
                                                        "MATERIAL_RETURNED",
                                                        actorId,
                                                        savedReq.getAgencyId(),
                                                        savedReq.getDepartmentId(),
                                                        Map.of("note", Objects.toString(req.note(), ""), "items", updates.size())
                                                ))
                                                .then(getRequest(tenantId, savedReq.getId())));
                            });
                });
    }

    public Mono<MaterialRequestDto> closeRequest(UUID tenantId, UUID id, String actorId, CloseMaterialRequestRequest req) {
        Instant now = Instant.now();
        return loadRequest(tenantId, id)
                .flatMap(current -> {
                    if (STATUS_CLOSED.equals(current.getStatus())) {
                        return Mono.error(new IllegalStateException("Request is already closed."));
                    }
                    current.setStatus(STATUS_CLOSED);
                    current.setClosedBy(actorId);
                    current.setClosedAt(now);
                    current.setCloseReason(req == null ? null : req.reason());
                    current.setUpdatedAt(now);
                    return requestRepository.save(current);
                })
                .flatMap(saved -> appendTrace(
                        tenantId,
                        ENTITY_TYPE_REQUEST,
                        saved.getId(),
                        "REQUEST_CLOSED",
                        actorId,
                        saved.getAgencyId(),
                        saved.getDepartmentId(),
                        req == null ? null : Map.of("reason", Objects.toString(req.reason(), ""))
                ).then(getRequest(tenantId, saved.getId())));
    }

    public Flux<TraceEventDto> listTraceEventsForRequest(UUID tenantId, UUID requestId) {
        return loadRequest(tenantId, requestId)
                .thenMany(traceEventRepository.findForEntity(tenantId, ENTITY_TYPE_REQUEST, requestId)
                        .map(MaterialOperationsService::toDto));
    }

    private Mono<MaterialRequestEntity> loadRequest(UUID tenantId, UUID id) {
        return requestRepository.findByTenantAndId(tenantId, id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Material request not found.")));
    }

    private Mono<Void> saveRequestItems(UUID tenantId,
                                        UUID requestId,
                                        Map<UUID, MaterialRequestItemInput> items,
                                        Instant now) {
        List<MaterialRequestItemEntity> rows = new ArrayList<>();
        for (MaterialRequestItemInput input : items.values()) {
            rows.add(MaterialRequestItemEntity.builder()
                    .id(UUID.randomUUID())
                    .newEntity(true)
                    .tenantId(tenantId)
                    .requestId(requestId)
                    .productId(input.productId())
                    .quantityRequested(input.quantity())
                    .quantityIssued(0)
                    .quantityReturned(0)
                    .note(input.note())
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
        }
        return itemRepository.saveAll(rows).then();
    }

    private Mono<MaterialRequestDto> toRequestDto(UUID tenantId, MaterialRequestEntity request) {
        return itemRepository.findByTenantAndRequestId(tenantId, request.getId())
                .map(MaterialOperationsService::toDto)
                .collectList()
                .map(items -> toDto(request, items));
    }

    private Mono<Void> appendTrace(UUID tenantId,
                                   String entityType,
                                   UUID entityId,
                                   String eventType,
                                   String actorId,
                                   UUID agencyId,
                                   UUID departmentId,
                                   Object payload) {
        String payloadJson = null;
        try {
            if (payload != null) payloadJson = objectMapper.writeValueAsString(payload);
        } catch (Exception ignored) {
            payloadJson = null;
        }

        TraceEventEntity event = TraceEventEntity.builder()
                .id(UUID.randomUUID())
                .newEntity(true)
                .tenantId(tenantId)
                .entityType(entityType)
                .entityId(entityId)
                .eventType(eventType)
                .actorId(actorId)
                .agencyId(agencyId)
                .departmentId(departmentId)
                .payloadJson(payloadJson)
                .createdAt(Instant.now())
                .build();
        return traceEventRepository.save(event).then();
    }

    private Map<UUID, MaterialRequestItemInput> validateAndIndexCreateItems(List<MaterialRequestItemInput> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("At least one request item is required.");
        }

        Map<UUID, MaterialRequestItemInput> out = new LinkedHashMap<>();
        for (MaterialRequestItemInput item : items) {
            if (item == null || item.productId() == null || item.quantity() == null || item.quantity() <= 0) {
                throw new IllegalArgumentException("Each request item must have productId and quantity > 0.");
            }
            if (out.containsKey(item.productId())) {
                throw new IllegalArgumentException("Duplicate product in request items: " + item.productId());
            }
            out.put(item.productId(), item);
        }
        return out;
    }

    private Map<UUID, MaterialQuantityUpdate> validateAndIndexUpdates(List<MaterialQuantityUpdate> updates) {
        if (updates == null || updates.isEmpty()) {
            throw new IllegalArgumentException("At least one item is required.");
        }

        Map<UUID, MaterialQuantityUpdate> out = new LinkedHashMap<>();
        for (MaterialQuantityUpdate item : updates) {
            if (item == null || item.productId() == null || item.quantity() == null || item.quantity() <= 0) {
                throw new IllegalArgumentException("Each item must have productId and quantity > 0.");
            }
            if (out.containsKey(item.productId())) {
                throw new IllegalArgumentException("Duplicate product in action items: " + item.productId());
            }
            out.put(item.productId(), item);
        }
        return out;
    }

    private void applyIssueUpdates(List<MaterialRequestItemEntity> items, Map<UUID, MaterialQuantityUpdate> updates) {
        Map<UUID, MaterialRequestItemEntity> byProduct = indexByProduct(items);
        for (MaterialQuantityUpdate update : updates.values()) {
            MaterialRequestItemEntity row = byProduct.get(update.productId());
            if (row == null) {
                throw new IllegalArgumentException("Product " + update.productId() + " is not part of this request.");
            }

            int requested = safe(row.getQuantityRequested());
            int issued = safe(row.getQuantityIssued());
            int nextIssued = issued + update.quantity();
            if (nextIssued > requested) {
                throw new IllegalStateException("Cannot issue above requested quantity for product " + update.productId());
            }

            row.setQuantityIssued(nextIssued);
            if (update.note() != null && !update.note().isBlank()) {
                row.setNote(update.note().trim());
            }
        }
    }

    private void applyReturnUpdates(List<MaterialRequestItemEntity> items, Map<UUID, MaterialQuantityUpdate> updates) {
        Map<UUID, MaterialRequestItemEntity> byProduct = indexByProduct(items);
        for (MaterialQuantityUpdate update : updates.values()) {
            MaterialRequestItemEntity row = byProduct.get(update.productId());
            if (row == null) {
                throw new IllegalArgumentException("Product " + update.productId() + " is not part of this request.");
            }

            int issued = safe(row.getQuantityIssued());
            int returned = safe(row.getQuantityReturned());
            int availableToReturn = issued - returned;
            if (update.quantity() > availableToReturn) {
                throw new IllegalStateException("Cannot return above issued quantity for product " + update.productId());
            }

            row.setQuantityReturned(returned + update.quantity());
            if (update.note() != null && !update.note().isBlank()) {
                row.setNote(update.note().trim());
            }
        }
    }

    private Map<UUID, MaterialRequestItemEntity> indexByProduct(List<MaterialRequestItemEntity> items) {
        Map<UUID, MaterialRequestItemEntity> out = new HashMap<>();
        for (MaterialRequestItemEntity item : items) {
            out.put(item.getProductId(), item);
        }
        return out;
    }

    private boolean isFullyIssued(MaterialRequestItemEntity item) {
        return safe(item.getQuantityIssued()) >= safe(item.getQuantityRequested());
    }

    private List<MaterialRequestItemEntity> markExisting(List<MaterialRequestItemEntity> items, Instant now) {
        List<MaterialRequestItemEntity> updated = new ArrayList<>(items.size());
        for (MaterialRequestItemEntity item : items) {
            item.setNewEntity(false);
            item.setUpdatedAt(now);
            updated.add(item);
        }
        return updated;
    }

    private static int safe(Integer value) {
        return value == null ? 0 : value;
    }

    private static String normalizeCode(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Department code is required.");
        }
        return input.trim().toUpperCase(Locale.ROOT);
    }

    private static String normalizeStatus(String status) {
        if (status == null || status.isBlank()) return null;
        return status.trim().toUpperCase(Locale.ROOT);
    }

    private static String normalizeReasonCode(String reasonCode) {
        if (reasonCode == null || reasonCode.isBlank()) return null;
        return reasonCode.trim().toUpperCase(Locale.ROOT);
    }

    private static String normalizeUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User id is required.");
        }
        return userId.trim();
    }

    private static DepartmentDto toDto(DepartmentEntity e) {
        return new DepartmentDto(
                e.getId(),
                e.getAgencyId(),
                e.getCode(),
                e.getName(),
                e.getActive(),
                e.getUpdatedAt()
        );
    }

    private static MaterialRequestItemDto toDto(MaterialRequestItemEntity e) {
        return new MaterialRequestItemDto(
                e.getProductId(),
                e.getQuantityRequested(),
                e.getQuantityIssued(),
                e.getQuantityReturned(),
                e.getNote()
        );
    }

    private static MaterialRequestDto toDto(MaterialRequestEntity e, List<MaterialRequestItemDto> items) {
        return new MaterialRequestDto(
                e.getId(),
                e.getAgencyId(),
                e.getDepartmentId(),
                e.getStatus(),
                e.getReasonCode(),
                e.getReasonText(),
                e.getRequestedBy(),
                e.getApprovedBy(),
                e.getApprovedAt(),
                e.getIssuedBy(),
                e.getIssuedAt(),
                e.getExpectedReturnAt(),
                e.getClosedBy(),
                e.getClosedAt(),
                e.getCloseReason(),
                e.getUpdatedAt(),
                items
        );
    }

    private static TraceEventDto toDto(TraceEventEntity e) {
        return new TraceEventDto(
                e.getId(),
                e.getEntityType(),
                e.getEntityId(),
                e.getEventType(),
                e.getActorId(),
                e.getAgencyId(),
                e.getDepartmentId(),
                e.getPayloadJson(),
                e.getCreatedAt()
        );
    }

    private static DepartmentMemberDto toDto(DepartmentMemberEntity e) {
        return new DepartmentMemberDto(
                e.getId(),
                e.getDepartmentId(),
                e.getUserId(),
                e.getUpdatedAt()
        );
    }
}
