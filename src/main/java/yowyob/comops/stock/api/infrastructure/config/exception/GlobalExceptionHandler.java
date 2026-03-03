package yowyob.comops.stock.api.infrastructure.config.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@Order(-2) // Priorité haute pour bypasser le handler par défaut de Spring Boot
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes, ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        final Throwable throwable = getError(request);
        HttpStatus status = determineHttpStatus(throwable);

        ErrorResponse errorResponse = new ErrorResponse(status, throwable.getMessage(), request.path());

        log.error("Stock API Error [{} {}]: Status={}, Message='{}'", request.methodName(), request.path(), status, throwable.getMessage());
        
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }

    private HttpStatus determineHttpStatus(Throwable throwable) {
        // Erreurs de sécurité Spring (@PreAuthorize failures)
        if (throwable instanceof AccessDeniedException) {
            return HttpStatus.FORBIDDEN; // 403
        }
        
        // Erreurs sur les arguments (IDs inexistants, contraintes violées)
        if (throwable instanceof IllegalArgumentException) {
            String message = throwable.getMessage().toLowerCase();
            if (message.contains("not found")) {
                return HttpStatus.NOT_FOUND; // 404
            }
            if (message.contains("already exists")) {
                return HttpStatus.CONFLICT; // 409
            }
            return HttpStatus.BAD_REQUEST; // 400
        }

        // Erreurs d'état métier (Actions impossibles vu l'état actuel)
        if (throwable instanceof IllegalStateException) {
            String message = throwable.getMessage().toLowerCase();
            
            // Rejet par Scope / Agence
            if (message.contains("access denied") || message.contains("another agency") || message.contains("involving your agency")) {
                return HttpStatus.FORBIDDEN; // 403
            }
            
            // Conflits d'état (Mouvement déjà validé, Produit avec du stock restant)
            if (message.contains("already validated") || message.contains("already cancelled") || message.contains("stock exists")) {
                return HttpStatus.CONFLICT; // 409
            }
            
            return HttpStatus.BAD_REQUEST; // 400
        }
        
        // Autres exceptions (Bugs, NullPointerException, DB Timeout...)
        log.error("Unhandled exception occurred", throwable);
        return HttpStatus.INTERNAL_SERVER_ERROR; // 500
    }
}