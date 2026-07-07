package com.emras.category.controller;
import com.emras.category.dto.request.CategoryRequest;
import com.emras.category.dto.response.CategoryResponse;
import com.emras.category.service.CategoryService;
import com.emras.shared.constant.ApiConstants;
import com.emras.shared.constant.SuccessMessages;
import com.emras.shared.model.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.CATEGORY_BASE)
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // ── Public endpoints ──────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.CATEGORIES_FETCHED,
                categoryService.getAllCategories(),
                HttpStatus.OK));
    }

    @GetMapping("/gender/{genderTarget}")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoriesByGender(
            @PathVariable String genderTarget) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.CATEGORIES_FETCHED,
                categoryService.getCategoriesByGender(genderTarget),
                HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.CATEGORY_FETCHED,
                categoryService.getCategoryById(id),
                HttpStatus.OK));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryBySlug(
            @PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.CATEGORY_FETCHED,
                categoryService.getCategoryBySlug(slug),
                HttpStatus.OK));
    }

    // ── Admin endpoints ───────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        SuccessMessages.CATEGORY_CREATED,
                        categoryService.createCategory(request),
                        HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.CATEGORY_UPDATED,
                categoryService.updateCategory(id, request),
                HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.CATEGORY_DELETED, HttpStatus.OK));
    }
}