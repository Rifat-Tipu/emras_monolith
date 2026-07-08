package com.emras.product.dto.request;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 200)
    private String name;

    @NotBlank(message = "Slug is required")
    @Size(max = 220)
    private String slug;

    private String description;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @Size(max = 100)
    private String brand;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    private BigDecimal discountPrice;

    private boolean featured;

    private String tags;

    private List<VariantRequest> variants;

    @Getter
    @Setter
    public static class VariantRequest {
        @NotBlank
        private String size;
        private String color;
        @NotBlank
        private String sku;
        @Min(0)
        private int stockQuantity;
        private BigDecimal priceModifier;
    }
}