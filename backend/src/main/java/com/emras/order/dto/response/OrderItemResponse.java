package com.emras.order.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderItemResponse {

    private final Long       orderItemId;
    private final Long       variantId;
    private final String     productName;
    private final String     size;
    private final String     color;
    private final String     sku;
    private final int        quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal lineTotal;
}