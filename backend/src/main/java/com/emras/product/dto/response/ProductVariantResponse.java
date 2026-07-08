package com.emras.product.dto.response;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class ProductVariantResponse {

    private final Long       id;
    private final String     size;
    private final String     color;
    private final String     sku;
    private final int        stockQuantity;
    private final BigDecimal priceModifier;
    private final BigDecimal effectivePrice;
    private final boolean    active;
    private final boolean    inStock;
}