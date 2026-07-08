package com.emras.product.repository;
import com.emras.product.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    Optional<ProductVariant> findByIdAndActiveTrue(Long id);

    List<ProductVariant> findByProductIdAndActiveTrue(Long productId);

    boolean existsBySku(String sku);

    @Modifying
    @Query("UPDATE ProductVariant pv SET pv.stockQuantity = pv.stockQuantity - :qty WHERE pv.id = :id AND pv.stockQuantity >= :qty")
    int deductStock(@Param("id") Long id, @Param("qty") int qty);

    @Modifying
    @Query("UPDATE ProductVariant pv SET pv.stockQuantity = pv.stockQuantity + :qty WHERE pv.id = :id")
    void restoreStock(@Param("id") Long id, @Param("qty") int qty);
}