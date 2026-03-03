package yowyob.comops.stock.api.infrastructure.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.stock.api.domain.model.Product;
import yowyob.comops.stock.api.domain.model.ProductCategory;
import yowyob.comops.stock.api.domain.port.in.ProductUseCase;
import yowyob.comops.stock.api.infrastructure.config.security.SecurityUtils;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Catalog", description = "Gestion du référentiel produit")
public class ProductController {
    private final ProductUseCase productService;
    private final SecurityUtils securityUtils;

    // --- PRODUCTS ---

    @GetMapping
    @Operation(summary = "Lister les produits")
    @PreAuthorize("hasAuthority('PRODUCT:READ')") // Accessible à tous les rôles logistiques
    public Flux<Product> getProducts() {
        return securityUtils.getCurrentOrganizationId()
                .flatMapMany(productService::getAllProducts);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT:READ')")
    public Mono<ResponseEntity<Product>> getProduct(@PathVariable UUID id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer un produit")
    @PreAuthorize("hasAuthority('PRODUCT:MANAGE')") // Réservé Stock Manager / Admin
    public Mono<ResponseEntity<Product>> createProduct(@RequestBody ProductRequest request) {
        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .categoryId(request.getCategoryId())
                .unit(request.getUnit())
                .isStockable(request.isStockable())
                .isPerishable(request.isPerishable())
                .defaultSalePrice(request.getDefaultSalePrice())
                .defaultCostPrice(request.getDefaultCostPrice())
                .minStockLevel(request.getMinStockLevel())
                .maxStockLevel(request.getMaxStockLevel())
                .build();

        return securityUtils.getCurrentOrganizationId()
                .flatMap(orgId -> {
                    product.setOrganizationId(orgId);
                    return productService.createProduct(product);
                })
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(p));
    }

    // --- CATEGORIES ---

    @GetMapping("/categories")
    @PreAuthorize("hasAuthority('PRODUCT:READ')")
    public Flux<ProductCategory> getCategories() {
        return securityUtils.getCurrentOrganizationId()
                .flatMapMany(productService::getAllCategories);
    }

    @PostMapping("/categories")
    @Operation(summary = "Créer une catégorie")
    @PreAuthorize("hasAuthority('PRODUCT:MANAGE')")
    public Mono<ResponseEntity<ProductCategory>> createCategory(@RequestBody CategoryRequest request) {
        ProductCategory category = ProductCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .parentId(request.getParentId())
                .build();

        return securityUtils.getCurrentOrganizationId()
                .flatMap(orgId -> {
                    category.setOrganizationId(orgId);
                    return productService.createCategory(category);
                })
                .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(c));
    }

    // --- DTOs (Gardés ici pour la concision) ---
    @Data
    public static class ProductRequest {
        private String sku;
        private String name;
        private String description;
        private UUID categoryId;
        private String unit;
        private boolean isStockable = true;
        private boolean isPerishable = false;
        private BigDecimal defaultSalePrice;
        private BigDecimal defaultCostPrice;
        private Integer minStockLevel;
        private Integer maxStockLevel;
    }

    @Data
    public static class CategoryRequest {
        private String name;
        private String description;
        private UUID parentId;
    }
}