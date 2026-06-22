package com.emras.product.facade;

import com.emras.product.dto.response.ProductSummaryResponse;
import com.emras.product.dto.response.VariantStockResponse;

import java.util.List;

/**
 * Public API of the Product domain.
 *
 * Cart, Order, AI, and any other domain that needs product or stock data
 * must ONLY depend on this interface.
 *
 * Internal classes (ProductService, ProductRepository, Product entity)
 * are private to the product package and must NOT be imported elsewhere.
 */
public interface ProductFacade {

    /**
     * Check whether a product variant exists.
     *
     * @param variantId the product variant ID
     * @return true if the variant exists and is active
     */
    boolean variantExists(Long variantId);

    /**
     * Get current stock quantity for a variant.
     *
     * @param variantId the product variant ID
     * @return available stock quantity
     */
    int getStockQuantity(Long variantId);

    /**
     * Check if a variant has sufficient stock for a requested quantity.
     *
     * @param variantId         the product variant ID
     * @param requestedQuantity how many units are needed
     * @return true if stock is sufficient
     */
    boolean hasSufficientStock(Long variantId, int requestedQuantity);

    /**
     * Deduct stock after an order is placed.
     * Called by OrderFacade during checkout.
     *
     * @param variantId the product variant ID
     * @param quantity  units to deduct
     */
    void deductStock(Long variantId, int quantity);

    /**
     * Restore stock if an order is cancelled.
     *
     * @param variantId the product variant ID
     * @param quantity  units to restore
     */
    void restoreStock(Long variantId, int quantity);

    /**
     * Get a lightweight product summary (name, price, image).
     * Used by cart items, order line items, AI responses.
     *
     * @param variantId the product variant ID
     * @return ProductSummaryResponse
     */
    ProductSummaryResponse getVariantSummary(Long variantId);

    /**
     * Get stock info for multiple variants at once.
     * Used during cart validation and checkout.
     *
     * @param variantIds list of variant IDs
     * @return list of VariantStockResponse
     */
    List<VariantStockResponse> getStockForVariants(List<Long> variantIds);
}
