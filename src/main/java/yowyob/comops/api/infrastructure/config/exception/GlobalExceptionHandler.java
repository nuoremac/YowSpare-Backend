package yowyob.comops.api.infrastructure.config.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@Order(-2) // Doit avoir une priorité plus élevée que le gestionnaire par défaut de Spring
           // Boot
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes, ApplicationContext applicationContext,
            ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        final Map<String, Object> errorPropertiesMap = getErrorAttributes(request,
                org.springframework.boot.web.error.ErrorAttributeOptions.defaults());
        final Throwable throwable = getError(request);

        HttpStatus status = determineHttpStatus(throwable);

        ErrorResponse errorResponse = new ErrorResponse(status, throwable.getMessage(), request.path());

        log.error("API Error [{} {}]: Status={}, Message='{}'", request.method().name(), request.path(), status,
                throwable.getMessage());

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }

    private HttpStatus determineHttpStatus(Throwable throwable) {
        if (throwable instanceof BadCredentialsException || throwable instanceof UsernameNotFoundException) {
            return HttpStatus.UNAUTHORIZED;
        }

        if (throwable instanceof IllegalArgumentException) {
            String message = throwable.getMessage().toLowerCase();
            if (message.contains("not found")) {
                return HttpStatus.NOT_FOUND;
            }
            if (message.contains("already exists")) {
                return HttpStatus.CONFLICT;
            }
            return HttpStatus.BAD_REQUEST;
        }

        if (throwable instanceof IllegalStateException) {
            String message = throwable.getMessage().toLowerCase();
            if (message.contains("access denied") || message.contains("permission")
                    || message.contains("only the owner")) {
                return HttpStatus.FORBIDDEN;
            }
            if (message.contains("already") || message.contains("cannot delete")) {
                return HttpStatus.CONFLICT;
            }
            return HttpStatus.BAD_REQUEST;
        }

        // Pour toutes les autres exceptions non prévues
        log.error("Unhandled exception occurred", throwable);
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}