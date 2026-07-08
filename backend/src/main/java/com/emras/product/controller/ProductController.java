package com.emras.product.controller;
import com.emras.product.dto.request.ProductRequest;
import com.emras.product.dto.response.ProductResponse;
import com.emras.product.service.ProductService;
import com.emras.shared.constant.ApiConstants;
import com.emras.shared.constant.SuccessMessages;
import com.emras.shared.model.ApiResponse;
import com.emras.shared.model.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(ApiConstants.PRODUCT_BASE)
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ── Public endpoints ──────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "12")   int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, Math.min(size, ApiConstants.MAX_PAGE_SIZE), sort);

        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.PRODUCTS_FETCHED,
                productService.getProducts(categoryId, minPrice, maxPrice, brand, pageable),
                HttpStatus.OK));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> searchProducts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, Math.min(size, ApiConstants.MAX_PAGE_SIZE));
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.PRODUCTS_FETCHED,
                productService.searchProducts(q, pageable),
                HttpStatus.OK));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> getFeatured(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.PRODUCTS_FETCHED,
                productService.getFeaturedProducts(pageable),
                HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.PRODUCT_FETCHED,
                productService.getProductById(id),
                HttpStatus.OK));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySlug(
            @PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.PRODUCT_FETCHED,
                productService.getProductBySlug(slug),
                HttpStatus.OK));
    }

    // ── Admin endpoints ───────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        SuccessMessages.PRODUCT_CREATED,
                        productService.createProduct(request),
                        HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.PRODUCT_UPDATED,
                productService.updateProduct(id, request),
                HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.PRODUCT_DELETED, HttpStatus.OK));
    }

    @PatchMapping("/variants/{variantId}/stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateStock(
            @PathVariable Long variantId,
            @RequestParam int quantity) {
        productService.updateStock(variantId, quantity);
        return ResponseEntity.ok(ApiResponse.success(
                "Stock updated successfully", HttpStatus.OK));
    }
}