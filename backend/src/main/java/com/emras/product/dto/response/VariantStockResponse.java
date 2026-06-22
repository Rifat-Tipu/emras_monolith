package com.emras.product.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Stock status for a single variant.
 * Used in bulk stock checks during cart validation and checkout.
 */
@Getter
@Builder
public class VariantStockResponse {

    private final Long    variantId;
    private final int     availableQuantity;
    private final boolean inStock;
}
