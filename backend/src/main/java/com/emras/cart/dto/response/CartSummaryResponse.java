package com.emras.cart.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Lightweight cart summary exposed via CartFacade.
 * Used by the Order domain during checkout to build order line items.
 */
@Getter
@Builder
public class CartSummaryResponse {

    private final Long            cartId;
    private final List<CartItemSummary> items;
    private final BigDecimal      subtotal;
    private final int             totalItems;

    @Getter
    @Builder
    public static class CartItemSummary {
        private final Long       cartItemId;
        private final Long       variantId;
        private final String     productName;
        private final String     size;
        private final String     color;
        private final int        quantity;
        private final BigDecimal unitPrice;
        private final BigDecimal lineTotal;
    }
}
