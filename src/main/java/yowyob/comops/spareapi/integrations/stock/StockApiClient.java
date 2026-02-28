package yowyob.comops.spareapi.integrations.stock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class StockApiClient {
    private final WebClient webClient;

    public StockApiClient(WebClient.Builder builder,
                          @Value("${app.stock.base-url:http://localhost:8081}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<List<StockLevelResponse>> getStockLevels(String authorization, String tenantId) {
        return webClient.get()
                .uri("/stock-levels")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .header("X-Tenant-ID", tenantId)
                .retrieve()
                .bodyToFlux(StockLevelResponse.class)
                .collectList();
    }

    public Mono<List<StockMovementResponse>> getMovements(String authorization, String tenantId) {
        return webClient.get()
                .uri("/movements")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .header("X-Tenant-ID", tenantId)
                .retrieve()
                .bodyToFlux(StockMovementResponse.class)
                .collectList();
    }
}

