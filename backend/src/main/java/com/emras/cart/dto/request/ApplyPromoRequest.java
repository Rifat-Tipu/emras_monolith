package com.emras.cart.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyPromoRequest {

    @NotBlank(message = "Promo code is required")
    private String code;
}