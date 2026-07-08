package com.emras.product.repository;

import com.emras.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySlugAndActiveTrue(String slug);

    boolean existsBySlug(String slug);

    @Query("""
            SELECT p FROM Product p
            WHERE p.active = true
            AND (:categoryId IS NULL OR p.category.id = :categoryId)
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            AND (CAST(:brand AS string) IS NULL OR LOWER(p.brand) = LOWER(CAST(:brand AS string)))
            """)
    Page<Product> findAllWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("minPrice")   BigDecimal minPrice,
            @Param("maxPrice")   BigDecimal maxPrice,
            @Param("brand")      String brand,
            Pageable pageable);

    @Query("""
            SELECT p FROM Product p
            WHERE p.active = true
            AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(p.tags) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Product> findByActiveTrueAndFeaturedTrue(Pageable pageable);
}