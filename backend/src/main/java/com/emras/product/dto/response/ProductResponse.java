package com.emras.product.dto.response;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class ProductResponse {
    private final Long                      id;
    private final String                    name;
    private final String                    slug;
    private final String                    description;
    private final Long                      categoryId;
    private final String                    categoryName;
    private final String                    brand;
    private final BigDecimal                price;
    private final BigDecimal                discountPrice;
    private final boolean                   featured;
    private final boolean                   active;
    private final String                    tags;
    private final String                    primaryImageUrl;
    private final List<ProductImageResponse>   images;
    private final List<ProductVariantResponse> variants;
    private final Instant                   createdAt;
}