package com.emras.order.model;
import com.emras.product.model.ProductVariant;
import com.emras.shared.model.AuditModel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends AuditModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Nullable — variant may be deleted but order item must remain
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    // Snapshot of product info at time of order (never changes even if product is updated)
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(nullable = false, length = 20)
    private String size;

    @Column(length = 50)
    private String color;

    @Column(nullable = false, length = 100)
    private String sku;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "line_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal lineTotal;
}