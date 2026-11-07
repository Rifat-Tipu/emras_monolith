package com.emras.order.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceOrderRequest {

    @NotNull(message = "Address is required")
    private Long addressId;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;  // CASH_ON_DELIVERY, BKASH_MANUAL, NAGAD_MANUAL

    private String promoCode;

    private String notes;
}