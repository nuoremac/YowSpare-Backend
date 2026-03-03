package yowyob.comops.stock.api.infrastructure.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "ComOps Stock API", version = "v1", description = "Gestion des produits et des stocks"), security = {
        @SecurityRequirement(name = "bearerAuth"), @SecurityRequirement(name = "tenantHeader") })
@SecurityScheme(name = "bearerAuth", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
@SecurityScheme(name = "tenantHeader", description = "ID de l'Organisation", paramName = "X-Tenant-ID", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
public class SwaggerConfig {
}