package yowyob.comops.stock.api.infrastructure.config.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

@Getter
public class ErrorResponse {
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final Instant timestamp;

    public ErrorResponse(HttpStatus httpStatus, String message, String path) {
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.path = path;
        this.timestamp = Instant.now();
    }

    public ErrorResponse(HttpStatus httpStatus, Map<String, Object> errorAttributes) {
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = (String) errorAttributes.getOrDefault("message", "An unexpected error occurred.");
        this.path = (String) errorAttributes.get("path");
        this.timestamp = Instant.now();
    }
}