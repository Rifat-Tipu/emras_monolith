package com.emras.category.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Slug is required")
    @Size(max = 120)
    private String slug;

    private String description;
    private String genderTarget;
    private String imageUrl;
    private Long   parentId;
    private int    sortOrder;
}