package com.emras.order.facade;

import com.emras.order.dto.response.OrderSummaryResponse;

import java.util.List;

/**
 * Public API of the Order domain.
 *
 * The Payment domain uses this to retrieve order details for payment
 * verification and to update order status after payment confirmation.
 *
 * The AI domain uses this to fetch order status and history
 * on behalf of the authenticated user.
 */
public interface OrderFacade {

    /**
     * Get a lightweight order summary by order ID.
     * Used by the payment domain and AI domain.
     *
     * @param orderId the order ID
     * @param userId  the requesting user's ID (for ownership validation)
     * @return OrderSummaryResponse
     */
    OrderSummaryResponse getOrderSummary(Long orderId, Long userId);

    /**
     * Get recent orders for a user.
     * Used by the AI domain for order history queries.
     *
     * @param userId the authenticated user's ID
     * @param limit  max number of orders to return
     * @return list of OrderSummaryResponse
     */
    List<OrderSummaryResponse> getRecentOrders(Long userId, int limit);

    /**
     * Mark an order's payment as verified.
     * Called by the Payment domain after successful payment verification.
     *
     * @param orderId the order ID
     */
    void markPaymentVerified(Long orderId);

    /**
     * Mark an order's payment as failed/rejected.
     * Called by the Payment domain after payment rejection.
     *
     * @param orderId the order ID
     */
    void markPaymentFailed(Long orderId);
}
