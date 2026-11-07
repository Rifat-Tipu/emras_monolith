package com.emras.order.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class OrderResponse {

    private final Long                   id;
    private final String                 orderNumber;
    private final String                 status;
    private final String                 paymentStatus;
    private final BigDecimal             subtotal;
    private final BigDecimal             discountAmount;
    private final BigDecimal             totalAmount;
    private final String                 promoCode;
    private final String                 notes;
    private final List<OrderItemResponse> items;
    private final Instant                placedAt;

    // Address snapshot
    private final String                 recipientName;
    private final String                 phone;
    private final String                 street;
    private final String                 area;
    private final String                 city;
    private final String                 district;
}