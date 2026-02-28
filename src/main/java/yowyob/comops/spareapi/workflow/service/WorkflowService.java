package yowyob.comops.spareapi.workflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.spareapi.workflow.dto.CreateWorkflowRequest;
import yowyob.comops.spareapi.workflow.dto.WorkflowRequestDto;
import yowyob.comops.spareapi.workflow.entity.WorkflowRequestEntity;
import yowyob.comops.spareapi.workflow.repository.WorkflowRequestRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class WorkflowService {
    private final WorkflowRequestRepository repository;
    private final ObjectMapper objectMapper;

    public WorkflowService(WorkflowRequestRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public Flux<WorkflowRequestDto> search(UUID tenantId, String status, String type) {
        String s = status == null || status.isBlank() ? null : status.trim().toUpperCase();
        String t = type == null || type.isBlank() ? null : type.trim().toUpperCase();
        return repository.search(tenantId, s, t).map(WorkflowService::toDto);
    }

    public Mono<WorkflowRequestDto> create(UUID tenantId, String requestedBy, CreateWorkflowRequest req) {
        Instant now = Instant.now();
        String payloadJson = null;
        try {
            if (req.payload() != null) payloadJson = objectMapper.writeValueAsString(req.payload());
        } catch (Exception ignored) {
            payloadJson = null;
        }
        WorkflowRequestEntity e = WorkflowRequestEntity.builder()
                .id(UUID.randomUUID())
                .newEntity(true)
                .tenantId(tenantId)
                .type(req.type().trim().toUpperCase())
                .status("PENDING")
                .requestedBy(requestedBy)
                .payloadJson(payloadJson)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return repository.save(e).map(WorkflowService::toDto);
    }

    public Mono<WorkflowRequestDto> approve(UUID tenantId, UUID id, String approvedBy) {
        Instant now = Instant.now();
        return repository.findByTenantAndId(tenantId, id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Workflow request not found.")))
                .flatMap(e -> {
                    e.setStatus("APPROVED");
                    e.setApprovedBy(approvedBy);
                    e.setApprovedAt(now);
                    e.setUpdatedAt(now);
                    return repository.save(e);
                })
                .map(WorkflowService::toDto);
    }

    public Mono<WorkflowRequestDto> reject(UUID tenantId, UUID id, String approvedBy) {
        Instant now = Instant.now();
        return repository.findByTenantAndId(tenantId, id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Workflow request not found.")))
                .flatMap(e -> {
                    e.setStatus("REJECTED");
                    e.setApprovedBy(approvedBy);
                    e.setApprovedAt(now);
                    e.setUpdatedAt(now);
                    return repository.save(e);
                })
                .map(WorkflowService::toDto);
    }

    private static WorkflowRequestDto toDto(WorkflowRequestEntity e) {
        return new WorkflowRequestDto(
                e.getId(),
                e.getType(),
                e.getStatus(),
                e.getRequestedBy(),
                e.getApprovedBy(),
                e.getApprovedAt(),
                e.getPayloadJson(),
                e.getUpdatedAt()
        );
    }
}
