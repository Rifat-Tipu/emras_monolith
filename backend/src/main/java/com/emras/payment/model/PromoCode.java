package com.emras.payment.model;

import com.emras.shared.model.AuditModel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "promo_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCode extends AuditModel {

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(length = 200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "min_order_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal minOrderAmount = BigDecimal.ZERO;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "used_count", nullable = false)
    @Builder.Default
    private int usedCount = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "expires_at")
    private Instant expiresAt;

    // ── Business logic helpers ────────────────────────────────────────────

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean isUsageLimitReached() {
        return usageLimit != null && usedCount >= usageLimit;
    }

    public boolean isApplicable(BigDecimal orderAmount) {
        return active
                && !isExpired()
                && !isUsageLimitReached()
                && orderAmount.compareTo(minOrderAmount) >= 0;
    }

    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (discountType == DiscountType.PERCENTAGE) {
            return orderAmount
                    .multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 2,
                            java.math.RoundingMode.HALF_UP);
        }
        // FLAT — can't discount more than the order amount
        return discountValue.min(orderAmount);
    }

    public void incrementUsage() {
        this.usedCount++;
    }
}