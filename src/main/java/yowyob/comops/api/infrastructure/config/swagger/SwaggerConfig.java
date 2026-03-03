package yowyob.comops.api.infrastructure.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "ComOps Core API",
        version = "v1",
        description = "API centrale pour la gestion des identités, organisations et configurations."
    ),
    // Applique les schémas de sécurité à TOUTES les routes
    security = {
        @SecurityRequirement(name = "bearerAuth"),
        @SecurityRequirement(name = "tenantHeader")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT Token",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
@SecurityScheme(
    name = "tenantHeader",
    description = "ID de l'Organisation (Tenant)",
    paramName = "X-Tenant-ID",
    type = SecuritySchemeType.APIKEY,
    in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
}