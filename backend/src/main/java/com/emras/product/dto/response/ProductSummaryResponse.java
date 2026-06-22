package com.emras.product.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Lightweight product+variant projection exposed via ProductFacade.
 * Used by cart items, order line items, AI response cards.
 */
@Getter
@Builder
public class ProductSummaryResponse {

    private final Long       variantId;
    private final Long       productId;
    private final String     productName;
    private final String     size;
    private final String     color;
    private final String     sku;
    private final BigDecimal price;
    private final String     primaryImageUrl;
    private final int        stockQuantity;
}
