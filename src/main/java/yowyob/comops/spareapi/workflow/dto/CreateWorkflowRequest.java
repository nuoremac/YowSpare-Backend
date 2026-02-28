package yowyob.comops.spareapi.workflow.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;

public record CreateWorkflowRequest(
        @NotBlank String type,
        JsonNode payload
) {
}

