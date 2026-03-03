package yowyob.comops.stock.api.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.Product;
import yowyob.comops.stock.api.domain.model.ProductCategory;
import yowyob.comops.stock.api.domain.port.in.ProductUseCase;
import yowyob.comops.stock.api.domain.port.out.CategoryRepositoryPort;
import yowyob.comops.stock.api.domain.port.out.ProductRepositoryPort;
import yowyob.comops.stock.api.domain.port.out.StockLevelRepositoryPort;
import yowyob.comops.stock.api.infrastructure.config.security.SecurityUtils;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductUseCase {
    private final ProductRepositoryPort productRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final StockLevelRepositoryPort stockLevelRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public Mono<Product> createProduct(Product product) {
        return productRepository.existsBySkuAndOrganizationId(product.getSku(), product.getOrganizationId())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new IllegalArgumentException(
                                "Product with SKU " + product.getSku() + " already exists."));
                    }
                    if (product.getCreatedAt() == null)
                        product.setCreatedAt(Instant.now());
                    return productRepository.save(product);
                });
    }

    @Override
    public Mono<Product> updateProduct(UUID id, Product product) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Product not found")))
                .flatMap(existing -> {
                    // Update allowed fields
                    if (product.getName() != null)
                        existing.setName(product.getName());
                    if (product.getDescription() != null)
                        existing.setDescription(product.getDescription());
                    if (product.getUnit() != null)
                        existing.setUnit(product.getUnit());
                    if (product.getDefaultSalePrice() != null)
                        existing.setDefaultSalePrice(product.getDefaultSalePrice());
                    if (product.getDefaultCostPrice() != null)
                        existing.setDefaultCostPrice(product.getDefaultCostPrice());
                    if (product.getMinStockLevel() != null)
                        existing.setMinStockLevel(product.getMinStockLevel());
                    if (product.getMaxStockLevel() != null)
                        existing.setMaxStockLevel(product.getMaxStockLevel());
                    if (product.getCategoryId() != null)
                        existing.setCategoryId(product.getCategoryId());

                    existing.setUpdatedAt(Instant.now());
                    return productRepository.save(existing);
                });
    }

    @Override
    public Mono<Product> getProductById(UUID id) {
        return productRepository.findById(id);
    }

    @Override
    public Flux<Product> getAllProducts(UUID organizationId) {
        // Logique de filtrage dynamique selon l'utilisateur connecté
        return securityUtils.getUserContext().flatMapMany(context -> {
            
            // 1. Si Staff Global (Pas d'agence ID) -> Voit TOUT le catalogue
            if (context.getAgencyId() == null) {
                return productRepository.findAllByOrganizationId(organizationId);
            }
            
            // 2. Si Staff Local (A un agency ID) -> Ne voit que les produits liés à son agence
            // (ceux qui ont une entrée dans stock_levels)
            return productRepository.findAllByOrganizationIdAndAgencyId(organizationId, context.getAgencyId());
        });
    }

    @Override
    @Transactional
    public Mono<Void> deleteProduct(UUID id) {
        // Règle Métier : Impossible de supprimer un produit s'il a du stock quelque part
        return stockLevelRepository.hasPositiveStockForProduct(id)
                .flatMap(hasStock -> {
                    if (Boolean.TRUE.equals(hasStock)) {
                        return Mono.error(new IllegalStateException("Cannot delete product: Stock exists in one or more agencies."));
                    }
                    // Si stock à 0 partout, on supprime le produit (et les stock_levels à 0 via cascade DB ou manuel)
                    return productRepository.deleteById(id);
                });
    }

    @Override
    public Mono<ProductCategory> createCategory(ProductCategory category) {
        return categoryRepository.save(category);
    }

    @Override
    public Flux<ProductCategory> getAllCategories(UUID organizationId) {
        return categoryRepository.findAllByOrganizationId(organizationId);
    }
}