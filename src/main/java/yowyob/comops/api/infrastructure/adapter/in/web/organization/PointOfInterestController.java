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
import yowyob.comops.api.domain.model.organization.PointOfInterest;
import yowyob.comops.api.domain.port.in.organization.PointOfInterestUseCase;
import java.util.UUID;

@RestController
@RequestMapping("/pois")
@RequiredArgsConstructor
@Tag(name = "Points of Interest", description = "Gestion des points d'intérêt géographiques")
public class PointOfInterestController {
    private final PointOfInterestUseCase poiService;

    @GetMapping
    @Operation(summary = "Lister tous les points d'intérêt", description = "Récupère la liste de tous les POI créés dans le système.")
    public Flux<PointOfInterest> getAllPois() {
        return poiService.getAllPois();
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau point d'intérêt")
    @ApiResponse(responseCode = "201", description = "Point d'intérêt créé avec succès", content = @Content(schema = @Schema(implementation = PointOfInterest.class)))
    public Mono<ResponseEntity<PointOfInterest>> createPoi(@RequestBody PoiRequest request) {
        PointOfInterest poi = PointOfInterest.builder()
                .name(request.getName())
                .type(request.getType())
                .description(request.getDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();

        return poiService.createPoi(poi)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created));
    }

    @PostMapping("/link")
    @Operation(summary = "Lier une agence à un point d'intérêt", description = "Crée une relation entre une agence et un POI, en spécifiant la distance et une description (ex: 'À 50m de notre agence').")
    @ApiResponse(responseCode = "201", description = "Lien créé avec succès")
    public Mono<ResponseEntity<Void>> linkAgencyToPoi(@RequestBody LinkRequest request) {
        return poiService
                .linkAgencyToPoi(request.getAgencyId(), request.getPoiId(), request.getDistanceMeters(),
                        request.getDescription())
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).<Void>build()));
    }

    @DeleteMapping("/link")
    @Operation(summary = "Délier une agence d'un point d'intérêt")
    @ApiResponse(responseCode = "204", description = "Lien supprimé avec succès")
    public Mono<ResponseEntity<Void>> unlinkAgencyFromPoi(@RequestParam UUID agencyId, @RequestParam UUID poiId) {
        return poiService.unlinkAgencyFromPoi(agencyId, poiId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    // -- DTOs de Requête --

    @Data
    @Schema(name = "PoiRequest", description = "Données pour la création d'un point d'intérêt")
    public static class PoiRequest {
        @Schema(example = "Hôtel Hilton")
        private String name;
        @Schema(example = "HOTEL", description = "Type de POI (ex: HOTEL, STATION, RESTAURANT)")
        private String type;
        @Schema(description = "Description du lieu")
        private String description;
        @Schema(example = "4.0483")
        private Double latitude;
        @Schema(example = "9.7043")
        private Double longitude;
    }

    @Data
    @Schema(name = "LinkRequest", description = "Données pour lier une agence et un POI")
    public static class LinkRequest {
        @Schema(description = "ID de l'agence concernée")
        private UUID agencyId;
        @Schema(description = "ID du point d'intérêt à lier")
        private UUID poiId;
        @Schema(example = "50", description = "Distance en mètres entre l'agence et le POI")
        private Integer distanceMeters;
        @Schema(example = "Juste en face de l'entrée principale")
        private String description;
    }
}