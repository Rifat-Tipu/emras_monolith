package com.emras.product.dto.response;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductImageResponse {
    private final Long    id;
    private final String  imageUrl;
    private final String  altText;
    private final int     sortOrder;
    private final boolean primary;
}