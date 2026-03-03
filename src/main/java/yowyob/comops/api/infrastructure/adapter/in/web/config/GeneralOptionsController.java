package yowyob.comops.api.infrastructure.adapter.in.web.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.config.AgencySettings;
import yowyob.comops.api.domain.model.config.AppBusinessSettings;
import yowyob.comops.api.domain.port.in.config.GeneralOptionsUseCase;
import yowyob.comops.api.infrastructure.config.exception.ErrorResponse;
import yowyob.comops.api.infrastructure.config.security.SecurityUtils;
import java.util.UUID;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
@Tag(name = "Settings", description = "Gestion des paramètres de l'organisation et des agences")
public class GeneralOptionsController {
    private final GeneralOptionsUseCase optionsService;
    private final SecurityUtils securityUtils;

    // --- GLOBAL (Organization) ---

    @GetMapping("/global")
    @Operation(summary = "Obtenir les paramètres globaux de l'organisation")
    @ApiResponse(responseCode = "200", description = "Paramètres de l'organisation",
        content = @Content(schema = @Schema(implementation = AppBusinessSettings.class)))
    public Mono<ResponseEntity<AppBusinessSettings>> getGlobalOptions() {
        return securityUtils.getCurrentOrganizationId()
                .flatMap(optionsService::getGlobalSettings)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/global")
    @Operation(summary = "Mettre à jour les paramètres globaux", description = "Nécessite des droits d'administration globale.")
    @ApiResponse(responseCode = "200", description = "Paramètres mis à jour")
    @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<ResponseEntity<AppBusinessSettings>> updateGlobalOptions(@RequestBody AppBusinessSettingsRequest request) {
        AppBusinessSettings settings = AppBusinessSettings.builder()
                .organizationPrefix(request.getOrganizationPrefix())
                .negotiateSellingPrice(request.isNegotiateSellingPrice())
                .sellingPriceIncludeVat(request.isSellingPriceIncludeVat())
                .authorizeExceptionalDiscount(request.isAuthorizeExceptionalDiscount())
                .grantableDiscountRate(request.getGrantableDiscountRate())
                .lengthOfVatInvoiceNumber(request.getLengthOfVatInvoiceNumber())
                .prefixOfVatInvoiceNumber(request.getPrefixOfVatInvoiceNumber())
                .lowStockAlert(request.isLowStockAlert())
                .preventiveMaintenanceAlert(request.isPreventiveMaintenanceAlert())
                .build();
        
        return securityUtils.getCurrentOrganizationId()
                .flatMap(orgId -> optionsService.updateGlobalSettings(orgId, settings))
                .map(ResponseEntity::ok);
    }

    // --- LOCAL (Agency) ---

    @GetMapping("/agency/{agencyId}")
    @Operation(summary = "Obtenir les paramètres d'une agence")
    @ApiResponse(responseCode = "200", description = "Paramètres de l'agence",
        content = @Content(schema = @Schema(implementation = AgencySettings.class)))
    public Mono<ResponseEntity<AgencySettings>> getAgencyOptions(@PathVariable UUID agencyId) {
        return optionsService.getAgencySettings(agencyId)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/agency/{agencyId}")
    @Operation(summary = "Mettre à jour les paramètres d'une agence", description = "Nécessite des droits de gestion sur l'agence.")
    @ApiResponse(responseCode = "200", description = "Paramètres mis à jour")
    @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<ResponseEntity<AgencySettings>> updateAgencyOptions(@PathVariable UUID agencyId, @RequestBody AgencySettingsRequest request) {
        AgencySettings settings = AgencySettings.builder()
                .agencyPrefix(request.getAgencyPrefix())
                .isPrintLogo(request.isPrintLogo())
                .paperFormat(request.getPaperFormat())
                .ticketFooterMessage(request.getTicketFooterMessage())
                .allowNegativeStock(request.isAllowNegativeStock())
                .defaultTimezone(request.getDefaultTimezone())
                .build();
        
        return optionsService.updateAgencySettings(agencyId, settings)
                .map(ResponseEntity::ok);
    }

    // -- DTOs de Requête --
    @Data
    @Schema(name = "AppBusinessSettingsRequest", description = "Champs modifiables pour les paramètres globaux de l'organisation")
    public static class AppBusinessSettingsRequest {
        private String organizationPrefix;
        private boolean negotiateSellingPrice;
        private boolean sellingPriceIncludeVat;
        private boolean authorizeExceptionalDiscount;
        private Double grantableDiscountRate;
        private Integer lengthOfVatInvoiceNumber;
        private String prefixOfVatInvoiceNumber;
        private boolean lowStockAlert;
        private boolean preventiveMaintenanceAlert;
    }
    
    @Data
    @Schema(name = "AgencySettingsRequest", description = "Champs modifiables pour les paramètres d'une agence")
    public static class AgencySettingsRequest {
        private String agencyPrefix;
        private boolean isPrintLogo;
        private String paperFormat;
        private String ticketFooterMessage;
        private boolean allowNegativeStock;
        private String defaultTimezone;
    }
}