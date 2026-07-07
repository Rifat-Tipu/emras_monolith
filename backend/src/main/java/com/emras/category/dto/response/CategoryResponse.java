package com.emras.category.dto.response;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class CategoryResponse {

    private final Long             id;
    private final String           name;
    private final String           slug;
    private final String           description;
    private final String           genderTarget;
    private final String           imageUrl;
    private final boolean          active;
    private final int              sortOrder;
    private final Long             parentId;
    private final List<CategoryResponse> children;
}