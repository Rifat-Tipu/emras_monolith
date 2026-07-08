package com.emras.product.model;
import com.emras.shared.model.AuditModel;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant extends AuditModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 20)
    private String size;

    @Column(length = 50)
    private String color;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(name = "stock_quantity", nullable = false)
    @Builder.Default
    private int stockQuantity = 0;

    @Column(name = "price_modifier",
            nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal priceModifier = BigDecimal.ZERO;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    public BigDecimal getEffectivePrice(BigDecimal basePrice) {
        return basePrice.add(priceModifier);
    }

    public boolean isInStock() {
        return stockQuantity > 0;
    }

    public boolean isLowStock(int threshold) {
        return stockQuantity > 0 && stockQuantity <= threshold;
    }
}