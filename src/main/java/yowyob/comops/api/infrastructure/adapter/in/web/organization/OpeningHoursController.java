package yowyob.comops.api.infrastructure.adapter.in.web.organization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.AgencySchedule;
import yowyob.comops.api.domain.model.organization.OpeningHoursRule;
import yowyob.comops.api.domain.model.organization.SpecialOpeningHours;
import yowyob.comops.api.domain.port.in.organization.OpeningHoursUseCase;
import yowyob.comops.api.infrastructure.config.exception.ErrorResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/agencies/{agencyId}/schedule")
@RequiredArgsConstructor
@Tag(name = "Agencies", description = "Gestion des agences, sièges sociaux et entrepôts")
public class OpeningHoursController {
    private final OpeningHoursUseCase openingHoursService;

    @GetMapping
    @Operation(summary = "Obtenir le planning complet d'une agence", description = "Retourne les horaires réguliers et les exceptions à venir.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AgencySchedule.class)))
    public Mono<ResponseEntity<AgencySchedule>> getSchedule(@PathVariable UUID agencyId) {
        return openingHoursService.getSchedule(agencyId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/regular")
    @Operation(summary = "Mettre à jour les horaires réguliers", description = "Remplace la totalité des horaires hebdomadaires pour une agence.")
    public Mono<ResponseEntity<List<OpeningHoursRule>>> updateRegularSchedule(
            @PathVariable UUID agencyId,
            @RequestBody List<OpeningHoursRule> rules) {
        return openingHoursService.updateRegularSchedule(agencyId, rules)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/exceptions")
    @Operation(summary = "Ajouter une fermeture/ouverture exceptionnelle")
    @ApiResponse(responseCode = "200", description = "Exception ajoutée", content = @Content(schema = @Schema(implementation = SpecialOpeningHours.class)))
    @ApiResponse(responseCode = "400", description = "Date dans le passé", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<ResponseEntity<SpecialOpeningHours>> addException(
            @PathVariable UUID agencyId,
            @RequestBody SpecialOpeningHours exception) {
        exception.setAgencyId(agencyId);
        return openingHoursService.addException(exception)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/exceptions/{exceptionId}")
    @Operation(summary = "Supprimer une exception d'horaire")
    @ApiResponse(responseCode = "204", description = "Exception supprimée")
    public Mono<ResponseEntity<Void>> removeException(
            @PathVariable UUID agencyId,
            @PathVariable UUID exceptionId) {
        return openingHoursService.removeException(exceptionId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @GetMapping("/status")
    @Operation(summary = "Vérifier si une agence est ouverte maintenant", description = "Retourne le statut actuel (OUVERT/FERMÉ) en tenant compte du fuseau horaire de l'agence.")
    public Mono<ResponseEntity<OpenStatus>> getStatus(@PathVariable UUID agencyId) {
        return openingHoursService.isOpenNow(agencyId)
                .map(isOpen -> ResponseEntity.ok(new OpenStatus(isOpen, isOpen ? "OPEN" : "CLOSED")))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Schema(description = "Statut d'ouverture actuel de l'agence")
    record OpenStatus(
            @Schema(description = "true si ouvert, false si fermé") boolean isOpen,
            @Schema(example = "OPEN") String status) {
    }
}