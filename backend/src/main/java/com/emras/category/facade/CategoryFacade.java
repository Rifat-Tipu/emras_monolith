package com.emras.category.facade;

/**
 * Public API of the Category domain.
 *
 * Product domain uses this to validate that a category exists
 * when creating or updating a product.
 *
 * Other domains should not need category details directly —
 * category info is embedded in product responses.
 */
public interface CategoryFacade {

    /**
     * Check whether a category exists and is active.
     *
     * @param categoryId the category ID
     * @return true if the category exists and is active
     */
    boolean existsById(Long categoryId);
}
