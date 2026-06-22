package com.emras.order.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lightweight order projection exposed via OrderFacade.
 * Used by Payment domain and AI domain.
 */
@Getter
@Builder
public class OrderSummaryResponse {

    private final Long       orderId;
    private final String     orderNumber;
    private final String     status;
    private final String     paymentStatus;
    private final BigDecimal totalAmount;
    private final Instant    placedAt;
}
