package yowyob.comops.stock.api.domain.port.out;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.Product;

import java.util.UUID;

public interface ProductRepositoryPort {
    Mono<Product> save(Product product);
    
    Mono<Product> findById(UUID id);
    
    // Vue Globale (Siège)
    Flux<Product> findAllByOrganizationId(UUID organizationId);
    
    // Vue Locale (Agence) : Ne retourne que les produits référencés dans cette agence
    Flux<Product> findAllByOrganizationIdAndAgencyId(UUID organizationId, UUID agencyId);
    
    Mono<Void> deleteById(UUID id);
    
    Mono<Boolean> existsBySkuAndOrganizationId(String sku, UUID organizationId);
}