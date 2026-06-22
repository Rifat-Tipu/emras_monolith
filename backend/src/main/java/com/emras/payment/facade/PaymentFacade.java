package com.emras.payment.facade;

import com.emras.payment.dto.response.PaymentStatusResponse;

/**
 * Public API of the Payment domain.
 *
 * The Order domain uses this to initiate a payment record when an order
 * is placed, and to query payment status.
 *
 * The AI domain uses this to apply promo codes during checkout.
 */
public interface PaymentFacade {

    /**
     * Get the current payment status for an order.
     * Used by the Order domain to include payment info in order responses.
     *
     * @param orderId the order ID
     * @return PaymentStatusResponse
     */
    PaymentStatusResponse getPaymentStatus(Long orderId);

    /**
     * Validate a promo code without applying it.
     * Used during cart/checkout to show discount preview.
     *
     * @param code            the promo code string
     * @param orderSubtotal   the cart subtotal before discount
     * @return true if the promo code is valid and applicable
     */
    boolean isPromoCodeValid(String code, java.math.BigDecimal orderSubtotal);
}
