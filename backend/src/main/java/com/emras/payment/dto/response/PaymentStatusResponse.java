package com.emras.payment.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lightweight payment status projection exposed via PaymentFacade.
 */
@Getter
@Builder
public class PaymentStatusResponse {

    private final Long       paymentId;
    private final Long       orderId;
    private final String     paymentMethod;  // COD, BKASH_MANUAL, NAGAD_MANUAL
    private final String     status;         // PENDING, SUBMITTED, VERIFIED, FAILED
    private final BigDecimal amount;
    private final Instant    verifiedAt;
}
