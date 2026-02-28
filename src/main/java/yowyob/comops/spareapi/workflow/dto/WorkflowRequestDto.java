package yowyob.comops.spareapi.workflow.dto;

import java.time.Instant;
import java.util.UUID;

public record WorkflowRequestDto(
        UUID id,
        String type,
        String status,
        String requestedBy,
        String approvedBy,
        Instant approvedAt,
        String payloadJson,
        Instant updatedAt
) {
}

