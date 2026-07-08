package com.emras.product.dto.response;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
/**
 * Lightweight product projection used by ProductFacade.
 * Used by cart items, order line items, and AI responses.
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