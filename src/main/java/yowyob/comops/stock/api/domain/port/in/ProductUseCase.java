package yowyob.comops.stock.api.domain.port.in;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.Product;
import yowyob.comops.stock.api.domain.model.ProductCategory;

import java.util.UUID;

public interface ProductUseCase {
    // Produits
    Mono<Product> createProduct(Product product);

    Mono<Product> updateProduct(UUID id, Product product);

    Mono<Product> getProductById(UUID id);

    Flux<Product> getAllProducts(UUID organizationId);

    Mono<Void> deleteProduct(UUID id);

    // Catégories
    Mono<ProductCategory> createCategory(ProductCategory category);

    Flux<ProductCategory> getAllCategories(UUID organizationId);
}