package yowyob.comops.spareapi.material.dto;

import java.time.Instant;
import java.util.UUID;

public record TraceEventDto(
        UUID id,
        String entityType,
        UUID entityId,
        String eventType,
        String actorId,
        UUID agencyId,
        UUID departmentId,
        String payloadJson,
        Instant createdAt
) {
}
