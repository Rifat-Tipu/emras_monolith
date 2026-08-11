package com.emras.cart.facade;

import com.emras.cart.dto.response.CartSummaryResponse;

/**
 * Public API of the Cart domain.
 * Other domains (Order, AI) use this interface only —
 * never CartService, CartRepository, or Cart entity directly.
 */
public interface CartFacade {

    CartSummaryResponse getCartSummary(Long userId);

    boolean isCartEmpty(Long userId);

    void clearCart(Long userId);

    void addItem(Long userId, Long variantId, int quantity);
}