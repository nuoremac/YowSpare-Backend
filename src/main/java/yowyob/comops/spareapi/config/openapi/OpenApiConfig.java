package yowyob.comops.spareapi.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class OpenApiConfig {
    public static final String SECURITY_SCHEME = "bearerAuth";
    public static final String TENANT_SCHEME = "tenantHeader";

    @Bean
    public OpenAPI spareApiOpenApi() {
        return new OpenAPI()
                .info(new Info().title("spare-api").version("v1"))
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT access token from core-api /auth/login")
                        )
                        .addSecuritySchemes(
                                TENANT_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-Tenant-ID")
                                        .description("ID de l'Organisation (tenant UUID)")
                        )
                )
                // Make swagger-ui show the "Authorize" button and apply both headers to operations by default.
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME)
                        .addList(TENANT_SCHEME));
    }

    // We intentionally do not add X-Tenant-ID as an operation parameter.
    // It's defined as an apiKey security scheme so users can set it once via the "Authorize" dialog.
}
