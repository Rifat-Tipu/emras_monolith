package com.emras.category.service.impl;
import com.emras.category.constant.CategoryConstants;
import com.emras.category.dto.request.CategoryRequest;
import com.emras.category.dto.response.CategoryResponse;
import com.emras.category.facade.CategoryFacade;
import com.emras.category.model.Category;
import com.emras.category.model.GenderTarget;
import com.emras.category.repository.CategoryRepository;
import com.emras.category.service.CategoryService;
import com.emras.shared.constant.ErrorMessages;
import com.emras.shared.exception.BusinessException;
import com.emras.shared.exception.ResourceNotFoundException;
import com.emras.shared.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService, CategoryFacade {
    private final CategoryRepository categoryRepository;
    // ── Public reads (cached) ─────────────────────────────────────────────
    @Override
    @Cacheable(value = CategoryConstants.CACHE_CATEGORIES, key = "'all'")
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        LogUtil.logServiceEntry("CategoryServiceImpl", "getAllCategories");
        List<Category> categories = categoryRepository.findAllRootCategories();
        return categories.stream().map(this::toResponse).toList();
    }
    @Override
    @Cacheable(value = CategoryConstants.CACHE_CATEGORIES, key = "#genderTarget")
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByGender(String genderTarget) {
        LogUtil.logServiceEntry("CategoryServiceImpl", "getCategoriesByGender",
                "gender", genderTarget);
        GenderTarget gender = parseGender(genderTarget);
        return categoryRepository
                .findByGenderTargetAndActiveTrueOrderBySortOrderAsc(gender)
                .stream().map(this::toResponse).toList();
    }
    @Override
    @Cacheable(value = CategoryConstants.CACHE_CATEGORIES, key = "'slug:' + #slug")
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        LogUtil.logServiceEntry("CategoryServiceImpl", "getCategoryBySlug", "slug", slug);
        return categoryRepository.findBySlug(slug)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));
    }
    @Override
    @Cacheable(value = CategoryConstants.CACHE_CATEGORIES, key = "'id:' + #id")
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        LogUtil.logServiceEntry("CategoryServiceImpl", "getCategoryById", "id", id);
        return categoryRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));
    }
    // ── Admin writes ──────────────────────────────────────────────────────
    @Override
    @CacheEvict(value = CategoryConstants.CACHE_CATEGORIES, allEntries = true)
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        LogUtil.logServiceEntry("CategoryServiceImpl", "createCategory");

        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new BusinessException(
                    "A category with this slug already exists",
                    ErrorMessages.Code.RESOURCE_ALREADY_EXISTS,
                    HttpStatus.CONFLICT
            );
        }
        Category category = Category.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .genderTarget(parseGender(request.getGenderTarget()))
                .imageUrl(request.getImageUrl())
                .sortOrder(request.getSortOrder())
                .active(true)
                .build();

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));
            category.setParent(parent);
        }
        categoryRepository.save(category);
        LogUtil.logBusinessEvent("CATEGORY_CREATED", "name", category.getName());
        return toResponse(category);
    }
    @Override
    @CacheEvict(value = CategoryConstants.CACHE_CATEGORIES, allEntries = true)
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        LogUtil.logServiceEntry("CategoryServiceImpl", "updateCategory", "id", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setGenderTarget(parseGender(request.getGenderTarget()));
        category.setImageUrl(request.getImageUrl());
        category.setSortOrder(request.getSortOrder());

        return toResponse(category);
    }
    @Override
    @CacheEvict(value = CategoryConstants.CACHE_CATEGORIES, allEntries = true)
    @Transactional
    public void deleteCategory(Long id) {
        LogUtil.logServiceEntry("CategoryServiceImpl", "deleteCategory", "id", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));
        category.setActive(false); // soft delete
        LogUtil.logBusinessEvent("CATEGORY_DELETED", "id", id);
    }
    // ── CategoryFacade impl ───────────────────────────────────────────────
    @Override
    public boolean existsById(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }
    // ── Helpers ───────────────────────────────────────────────────────────
    private CategoryResponse toResponse(Category c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .slug(c.getSlug())
                .description(c.getDescription())
                .genderTarget(c.getGenderTarget().name())
                .imageUrl(c.getImageUrl())
                .active(c.isActive())
                .sortOrder(c.getSortOrder())
                .parentId(c.getParent() != null ? c.getParent().getId() : null)
                .children(c.getChildren().stream()
                        .filter(Category::isActive)
                        .map(this::toResponse)
                        .toList())
                .build();
    }

    private GenderTarget parseGender(String value) {
        if (value == null || value.isBlank()) return GenderTarget.MEN;
        try {
            return GenderTarget.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "Invalid gender target: " + value,
                    ErrorMessages.Code.INVALID_INPUT,
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}