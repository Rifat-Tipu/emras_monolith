package com.emras.product.service;
import com.emras.product.dto.request.ProductRequest;
import com.emras.product.dto.response.ProductResponse;
import com.emras.shared.model.PagedResponse;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
public interface ProductService {

    PagedResponse<ProductResponse> getProducts(
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String brand,
            Pageable pageable);

    PagedResponse<ProductResponse> searchProducts(String keyword, Pageable pageable);

    PagedResponse<ProductResponse> getFeaturedProducts(Pageable pageable);

    ProductResponse getProductBySlug(String slug);

    ProductResponse getProductById(Long id);

    // Admin
    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    void updateStock(Long variantId, int quantity);
}