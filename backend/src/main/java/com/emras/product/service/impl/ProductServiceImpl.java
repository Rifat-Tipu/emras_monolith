package com.emras.product.service.impl;
import com.emras.category.repository.CategoryRepository;
import com.emras.product.constant.ProductConstants;
import com.emras.product.dto.request.ProductRequest;
import com.emras.product.dto.response.*;
import com.emras.product.facade.ProductFacade;
import com.emras.product.model.Product;
import com.emras.product.model.ProductImage;
import com.emras.product.model.ProductVariant;
import com.emras.product.repository.ProductRepository;
import com.emras.product.repository.ProductVariantRepository;
import com.emras.product.service.ProductService;
import com.emras.shared.constant.ErrorMessages;
import com.emras.shared.exception.BusinessException;
import com.emras.shared.exception.ResourceNotFoundException;
import com.emras.shared.model.PagedResponse;
import com.emras.shared.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService, ProductFacade {

    private final ProductRepository        productRepository;
    private final ProductVariantRepository variantRepository;
    private final CategoryRepository       categoryRepository;

    // ── Public reads ──────────────────────────────────────────────────────

    @Override
    @Cacheable(value = ProductConstants.CACHE_PRODUCTS,
            key = "#categoryId + '_' + #minPrice + '_' + #maxPrice + '_' + #brand + '_' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getProducts(Long categoryId,
                                                      BigDecimal minPrice,
                                                      BigDecimal maxPrice,
                                                      String brand,
                                                      Pageable pageable) {
        LogUtil.logServiceEntry("ProductServiceImpl", "getProducts");
        return PagedResponse.of(
                productRepository.findAllWithFilters(categoryId, minPrice, maxPrice, brand, pageable)
                        .map(this::toResponse));
    }

    @Override
    @Cacheable(value = ProductConstants.CACHE_SEARCH, key = "#keyword + '_' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        LogUtil.logServiceEntry("ProductServiceImpl", "searchProducts", "keyword", keyword);
        return PagedResponse.of(
                productRepository.searchByKeyword(keyword, pageable).map(this::toResponse));
    }

    @Override
    @Cacheable(value = ProductConstants.CACHE_FEATURED, key = "'featured_' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getFeaturedProducts(Pageable pageable) {
        LogUtil.logServiceEntry("ProductServiceImpl", "getFeaturedProducts");
        return PagedResponse.of(
                productRepository.findByActiveTrueAndFeaturedTrue(pageable).map(this::toResponse));
    }

    @Override
    @Cacheable(value = ProductConstants.CACHE_PRODUCT_DETAIL, key = "'slug:' + #slug")
    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
        LogUtil.logServiceEntry("ProductServiceImpl", "getProductBySlug", "slug", slug);
        return productRepository.findBySlugAndActiveTrue(slug)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));
    }

    @Override
    @Cacheable(value = ProductConstants.CACHE_PRODUCT_DETAIL, key = "'id:' + #id")
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        LogUtil.logServiceEntry("ProductServiceImpl", "getProductById", "id", id);
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));
    }

    // ── Admin writes ──────────────────────────────────────────────────────

    @Override
    @Caching(evict = {
            @CacheEvict(value = ProductConstants.CACHE_PRODUCTS,       allEntries = true),
            @CacheEvict(value = ProductConstants.CACHE_FEATURED,       allEntries = true),
            @CacheEvict(value = ProductConstants.CACHE_SEARCH,         allEntries = true)
    })
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        LogUtil.logServiceEntry("ProductServiceImpl", "createProduct");

        if (productRepository.existsBySlug(request.getSlug())) {
            throw new BusinessException(
                    "A product with this slug already exists",
                    ErrorMessages.Code.RESOURCE_ALREADY_EXISTS,
                    HttpStatus.CONFLICT
            );
        }

        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));

        Product product = Product.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .category(category)
                .brand(request.getBrand())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .featured(request.isFeatured())
                .tags(request.getTags())
                .active(true)
                .build();

        // Add variants
        if (request.getVariants() != null) {
            request.getVariants().forEach(vr -> {
                if (variantRepository.existsBySku(vr.getSku())) {
                    throw new BusinessException(
                            "SKU already exists: " + vr.getSku(),
                            ErrorMessages.Code.RESOURCE_ALREADY_EXISTS,
                            HttpStatus.CONFLICT
                    );
                }
                ProductVariant variant = ProductVariant.builder()
                        .product(product)
                        .size(vr.getSize())
                        .color(vr.getColor())
                        .sku(vr.getSku())
                        .stockQuantity(vr.getStockQuantity())
                        .priceModifier(vr.getPriceModifier() != null
                                ? vr.getPriceModifier() : BigDecimal.ZERO)
                        .build();
                product.getVariants().add(variant);
            });
        }

        productRepository.save(product);
        LogUtil.logBusinessEvent("PRODUCT_CREATED",
                "id", product.getId(), "name", product.getName());
        return toResponse(product);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = ProductConstants.CACHE_PRODUCTS,       allEntries = true),
            @CacheEvict(value = ProductConstants.CACHE_PRODUCT_DETAIL, allEntries = true),
            @CacheEvict(value = ProductConstants.CACHE_FEATURED,       allEntries = true),
            @CacheEvict(value = ProductConstants.CACHE_SEARCH,         allEntries = true)
    })
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        LogUtil.logServiceEntry("ProductServiceImpl", "updateProduct", "id", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));

        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setFeatured(request.isFeatured());
        product.setTags(request.getTags());

        return toResponse(product);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = ProductConstants.CACHE_PRODUCTS,       allEntries = true),
            @CacheEvict(value = ProductConstants.CACHE_PRODUCT_DETAIL, allEntries = true),
            @CacheEvict(value = ProductConstants.CACHE_FEATURED,       allEntries = true),
            @CacheEvict(value = ProductConstants.CACHE_SEARCH,         allEntries = true)
    })
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));
        product.setActive(false); // soft delete
        LogUtil.logBusinessEvent("PRODUCT_DELETED", "id", id);
    }

    @Override
    @Transactional
    public void updateStock(Long variantId, int quantity) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.VARIANT_NOT_FOUND));
        variant.setStockQuantity(quantity);
        LogUtil.logBusinessEvent("STOCK_UPDATED", "variantId", variantId, "qty", quantity);
    }

    // ── ProductFacade impl ────────────────────────────────────────────────

    @Override
    public boolean variantExists(Long variantId) {
        return variantRepository.existsById(variantId);
    }

    @Override
    public int getStockQuantity(Long variantId) {
        return variantRepository.findByIdAndActiveTrue(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.VARIANT_NOT_FOUND))
                .getStockQuantity();
    }

    @Override
    public boolean hasSufficientStock(Long variantId, int requestedQuantity) {
        return variantRepository.findByIdAndActiveTrue(variantId)
                .map(v -> v.getStockQuantity() >= requestedQuantity)
                .orElse(false);
    }

    @Override
    @Transactional
    public void deductStock(Long variantId, int quantity) {
        int updated = variantRepository.deductStock(variantId, quantity);
        if (updated == 0) {
            throw new BusinessException(
                    ErrorMessages.INSUFFICIENT_STOCK,
                    ErrorMessages.Code.CART_INSUFFICIENT_STOCK,
                    HttpStatus.CONFLICT
            );
        }
    }

    @Override
    @Transactional
    public void restoreStock(Long variantId, int quantity) {
        variantRepository.restoreStock(variantId, quantity);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductSummaryResponse getVariantSummary(Long variantId) {
        ProductVariant variant = variantRepository.findByIdAndActiveTrue(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.VARIANT_NOT_FOUND));
        Product product = variant.getProduct();

        String primaryImage = product.getImages().stream()
                .filter(ProductImage::isPrimary)
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(null);

        return ProductSummaryResponse.builder()
                .variantId(variant.getId())
                .productId(product.getId())
                .productName(product.getName())
                .size(variant.getSize())
                .color(variant.getColor())
                .sku(variant.getSku())
                .price(variant.getEffectivePrice(product.getPrice()))
                .primaryImageUrl(primaryImage)
                .stockQuantity(variant.getStockQuantity())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VariantStockResponse> getStockForVariants(List<Long> variantIds) {
        return variantIds.stream()
                .map(id -> variantRepository.findByIdAndActiveTrue(id)
                        .map(v -> VariantStockResponse.builder()
                                .variantId(v.getId())
                                .availableQuantity(v.getStockQuantity())
                                .inStock(v.isInStock())
                                .build())
                        .orElse(VariantStockResponse.builder()
                                .variantId(id)
                                .availableQuantity(0)
                                .inStock(false)
                                .build()))
                .toList();
    }

    // ── Mapping helpers ───────────────────────────────────────────────────

    private ProductResponse toResponse(Product p) {
        String primaryImage = p.getImages().stream()
                .filter(ProductImage::isPrimary)
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(null);

        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .slug(p.getSlug())
                .description(p.getDescription())
                .categoryId(p.getCategory().getId())
                .categoryName(p.getCategory().getName())
                .brand(p.getBrand())
                .price(p.getPrice())
                .discountPrice(p.getDiscountPrice())
                .featured(p.isFeatured())
                .active(p.isActive())
                .tags(p.getTags())
                .primaryImageUrl(primaryImage)
                .images(p.getImages().stream().map(this::toImageResponse).toList())
                .variants(p.getVariants().stream()
                        .filter(ProductVariant::isActive)
                        .map(v -> toVariantResponse(v, p.getPrice()))
                        .toList())
                .createdAt(p.getCreatedAt())
                .build();
    }

    private ProductImageResponse toImageResponse(ProductImage img) {
        return ProductImageResponse.builder()
                .id(img.getId())
                .imageUrl(img.getImageUrl())
                .altText(img.getAltText())
                .sortOrder(img.getSortOrder())
                .primary(img.isPrimary())
                .build();
    }

    private ProductVariantResponse toVariantResponse(ProductVariant v, BigDecimal basePrice) {
        return ProductVariantResponse.builder()
                .id(v.getId())
                .size(v.getSize())
                .color(v.getColor())
                .sku(v.getSku())
                .stockQuantity(v.getStockQuantity())
                .priceModifier(v.getPriceModifier())
                .effectivePrice(v.getEffectivePrice(basePrice))
                .active(v.isActive())
                .inStock(v.isInStock())
                .build();
    }
}