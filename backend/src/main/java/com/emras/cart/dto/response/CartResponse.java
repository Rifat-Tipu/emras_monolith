package com.emras.cart.dto.response;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class CartResponse {

    private final Long                   cartId;
    private final List<CartItemResponse> items;
    private final int                    totalItems;
    private final BigDecimal             subtotal;
}