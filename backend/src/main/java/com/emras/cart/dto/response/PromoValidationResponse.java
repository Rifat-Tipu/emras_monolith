package com.emras.cart.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PromoValidationResponse {

    private final String     code;
    private final String     discountType;
    private final BigDecimal discountValue;
    private final BigDecimal discountAmount;
    private final BigDecimal originalSubtotal;
    private final BigDecimal finalSubtotal;
    private final String     message;
}