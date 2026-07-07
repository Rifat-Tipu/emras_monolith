package com.emras.category.service;
import com.emras.category.dto.request.CategoryRequest;
import com.emras.category.dto.response.CategoryResponse;
import java.util.List;
public interface CategoryService {

    List<CategoryResponse> getAllCategories();

    List<CategoryResponse> getCategoriesByGender(String genderTarget);

    CategoryResponse getCategoryBySlug(String slug);

    CategoryResponse getCategoryById(Long id);

    // Admin operations
    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);
}