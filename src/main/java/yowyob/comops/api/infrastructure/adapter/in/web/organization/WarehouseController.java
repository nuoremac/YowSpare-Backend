package yowyob.comops.api.infrastructure.adapter.in.web.organization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.Agency;
import yowyob.comops.api.domain.port.in.organization.AgencyUseCase;
import yowyob.comops.api.infrastructure.config.security.SecurityUtils;
import java.util.UUID;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
@Tag(name = "Agencies", description = "Gestion des agences, sièges sociaux et entrepôts")
public class WarehouseController {
    private final AgencyUseCase agencyService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @Operation(summary = "Lister tous les entrepôts", description = "Récupère la liste de toutes les agences de type 'WAREHOUSE'.")
    public Flux<Agency> getAllWarehouses() {
        return securityUtils.getCurrentOrganizationId()
                .flatMapMany(agencyService::getAllWarehouses);
    }

    @PostMapping
    @Operation(summary = "Créer un nouvel entrepôt", description = "Raccourci pour créer une agence avec le type 'WAREHOUSE' prédéfini.")
    public Mono<ResponseEntity<Agency>> createWarehouse(@RequestBody AgencyController.AgencyRequest request) {
        Agency agency = Agency.builder()
                .name(request.getName())
                .type("WAREHOUSE") // Type forcé
                .isHeadquarter(false) // Un entrepôt n'est jamais un siège
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
}