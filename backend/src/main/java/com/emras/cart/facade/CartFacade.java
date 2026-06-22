package com.emras.cart.facade;

import com.emras.cart.dto.response.CartSummaryResponse;

/**
 * Public API of the Cart domain.
 *
 * The Order domain uses this to retrieve cart contents during checkout
 * and to clear the cart after a successful order.
 *
 * The AI domain uses this to add/remove items and view the cart
 * on behalf of the authenticated user.
 */
public interface CartFacade {

    /**
     * Get a summary of the user's current cart (items, totals).
     * Used by the order domain during checkout.
     *
     * @param userId the authenticated user's ID
     * @return CartSummaryResponse containing line items and totals
     */
    CartSummaryResponse getCartSummary(Long userId);

    /**
     * Check whether the user's cart is empty.
     *
     * @param userId the authenticated user's ID
     * @return true if the cart has no items
     */
    boolean isCartEmpty(Long userId);

    /**
     * Clear all items from the user's cart.
     * Called by the order domain after a successful order placement.
     *
     * @param userId the authenticated user's ID
     */
    void clearCart(Long userId);

    /**
     * Add a product variant to the user's cart.
     * Called by the AI domain via tool calling.
     *
     * @param userId    the authenticated user's ID
     * @param variantId the product variant ID
     * @param quantity  number of units to add
     */
    void addItem(Long userId, Long variantId, int quantity);

    /**
     * Remove a specific item from the user's cart.
     * Called by the AI domain via tool calling.
     *
     * @param userId     the authenticated user's ID
     * @param cartItemId the cart item ID to remove
     */
    void removeItem(Long userId, Long cartItemId);
}
