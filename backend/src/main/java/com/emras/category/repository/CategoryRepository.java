package com.emras.category.repository;
import com.emras.category.model.Category;
import com.emras.category.model.GenderTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);
    boolean existsBySlug(String slug);
    // Top-level categories (no parent) ordered by sortOrder
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.active = true ORDER BY c.sortOrder ASC")
    List<Category> findAllRootCategories();
    // All active categories by gender
    List<Category> findByGenderTargetAndActiveTrueOrderBySortOrderAsc(GenderTarget genderTarget);
}