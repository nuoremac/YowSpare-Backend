package yowyob.comops.stock.api.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import yowyob.comops.stock.api.domain.model.StockLevel;
import yowyob.comops.stock.api.domain.port.in.StockLevelUseCase;
import yowyob.comops.stock.api.infrastructure.config.security.SecurityUtils;

@RestController
@RequestMapping("/stock-levels")
@RequiredArgsConstructor
@Tag(name = "Stock Levels", description = "Consultation des quantités en stock")
public class StockLevelController {

    private final StockLevelUseCase stockLevelService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @Operation(summary = "Consulter les niveaux de stock",
        description = "Récupère les niveaux de stock. Si l'utilisateur est un staff global, retourne le stock de toutes les agences. " +
                      "Si c'est un staff local, retourne uniquement le stock de son agence.")
    @PreAuthorize("hasAuthority('STOCK:READ')")
    public Flux<StockLevel> getStockLevels() {
        return securityUtils.getCurrentOrganizationId()
                .flatMapMany(stockLevelService::getStockLevels);
    }
}