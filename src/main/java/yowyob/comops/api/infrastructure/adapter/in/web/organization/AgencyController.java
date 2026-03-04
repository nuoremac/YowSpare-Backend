package yowyob.comops.api.infrastructure.adapter.in.web.organization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.Agency;
import yowyob.comops.api.domain.port.in.organization.AgencyUseCase;
import yowyob.comops.api.infrastructure.config.exception.ErrorResponse;
import yowyob.comops.api.infrastructure.config.security.SecurityUtils;
import java.util.UUID;

@RestController
@RequestMapping("/agencies")
@RequiredArgsConstructor
@Tag(name = "Agencies", description = "Gestion des agences, sièges sociaux et entrepôts")
public class AgencyController {
    private final AgencyUseCase agencyService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @Operation(summary = "Lister les agences de l'organisation", description = "Récupère toutes les agences (siège, point de vente, entrepôt...) de l'organisation courante.")
    public Flux<Agency> getAgencies() {
        return securityUtils.getCurrentOrganizationId()
                .flatMapMany(agencyService::getAllAgencies);
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle agence", description = "Crée une nouvelle agence dans l'organisation courante. Le type (HQ, WAREHOUSE, etc.) peut être spécifié.")
    @ApiResponse(responseCode = "201", description = "Agence créée", content = @Content(schema = @Schema(implementation = Agency.class)))
    @ApiResponse(responseCode = "403", description = "Le plan de l'utilisateur ne permet pas de créer de nouvelles agences", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<ResponseEntity<Agency>> createAgency(@RequestBody AgencyRequest request) {
        Agency agency = Agency.builder()
                .name(request.getName())
                .type(request.getType())
                .isHeadquarter(request.isHeadquarter())
                .address(request.getAddress())
                .city(request.getCity())
                .timezone(request.getTimezone())
                .build();

        return securityUtils.getCurrentOrganizationId()
                .flatMap(orgId -> {
                    agency.setOrganizationId(orgId);
                    return agencyService.createAgency(agency);
                })
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre à jour une agence", description = "Met à jour partiellement les informations d'une agence existante.")
    @ApiResponse(responseCode = "200", description = "Agence mise à jour", content = @Content(schema = @Schema(implementation = Agency.class)))
    public Mono<ResponseEntity<Agency>> updateAgency(@PathVariable UUID id, @RequestBody AgencyRequest request) {
        Agency agency = Agency.builder()
                .name(request.getName())
                .type(request.getType())
                .isHeadquarter(request.isHeadquarter())
                .address(request.getAddress())
                .city(request.getCity())
                .timezone(request.getTimezone())
                .build();

        return agencyService.updateAgency(id, agency)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une agence")
    @ApiResponse(responseCode = "204", description = "Agence supprimée avec succès")
    @ApiResponse(responseCode = "409", description = "Impossible de supprimer (ex: l'agence a encore du stock, ou est un siège social)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<ResponseEntity<Void>> deleteAgency(@PathVariable UUID id) {
        return agencyService.deleteAgency(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @Data
    @Schema(name = "AgencyRequest", description = "Données pour créer ou mettre à jour une agence")
    public static class AgencyRequest {
        @Schema(example = "Entrepôt de Bonabéri")
        private String name;
        @Schema(example = "WAREHOUSE", description = "Types possibles: HQ, WAREHOUSE, POS, OFFICE")
        private String type;
        @Schema(description = "Définit si cette agence est le siège social (un seul par organisation)")
        private boolean isHeadquarter;
        @Schema(example = "Zone Industrielle, Bonabéri")
        private String address;
        @Schema(example = "Douala")
        private String city;
        @Schema(example = "Africa/Douala", description = "Fuseau horaire au format IANA")
        private String timezone;
    }
}