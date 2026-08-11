package com.emras.cart.dto.response;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class CartItemResponse {

    private final Long       cartItemId;
    private final Long       variantId;
    private final Long       productId;
    private final String     productName;
    private final String     size;
    private final String     color;
    private final String     sku;
    private final String     imageUrl;
    private final int        quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal lineTotal;
    private final int        availableStock;
    private final boolean    inStock;
}