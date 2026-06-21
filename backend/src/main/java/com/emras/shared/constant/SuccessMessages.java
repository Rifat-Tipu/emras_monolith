package com.emras.shared.constant;

public final class SuccessMessages {

    private SuccessMessages() {}

    // ── Auth ─────────────────────────────────────────────────────────────
    public static final String USER_REGISTERED        = "User registered successfully";
    public static final String LOGIN_SUCCESS          = "Login successful";
    public static final String TOKEN_REFRESHED        = "Token refreshed successfully";
    public static final String LOGOUT_SUCCESS         = "Logged out successfully";

    // ── User ─────────────────────────────────────────────────────────────
    public static final String USER_FETCHED           = "User fetched successfully";
    public static final String USER_UPDATED           = "User updated successfully";
    public static final String ADDRESS_ADDED          = "Address added successfully";
    public static final String ADDRESS_UPDATED        = "Address updated successfully";
    public static final String ADDRESS_DELETED        = "Address deleted successfully";

    // ── Product ──────────────────────────────────────────────────────────
    public static final String PRODUCTS_FETCHED       = "Products fetched successfully";
    public static final String PRODUCT_FETCHED        = "Product fetched successfully";
    public static final String PRODUCT_CREATED        = "Product created successfully";
    public static final String PRODUCT_UPDATED        = "Product updated successfully";
    public static final String PRODUCT_DELETED        = "Product deleted successfully";

    // ── Category ─────────────────────────────────────────────────────────
    public static final String CATEGORIES_FETCHED     = "Categories fetched successfully";
    public static final String CATEGORY_FETCHED       = "Category fetched successfully";
    public static final String CATEGORY_CREATED       = "Category created successfully";
    public static final String CATEGORY_UPDATED       = "Category updated successfully";
    public static final String CATEGORY_DELETED       = "Category deleted successfully";

    // ── Cart ─────────────────────────────────────────────────────────────
    public static final String CART_FETCHED           = "Cart fetched successfully";
    public static final String CART_ITEM_ADDED        = "Item added to cart successfully";
    public static final String CART_ITEM_UPDATED      = "Cart item updated successfully";
    public static final String CART_ITEM_REMOVED      = "Item removed from cart";
    public static final String CART_CLEARED           = "Cart cleared successfully";
    public static final String PROMO_APPLIED          = "Promo code applied successfully";

    // ── Order ────────────────────────────────────────────────────────────
    public static final String ORDER_PLACED           = "Order placed successfully";
    public static final String ORDER_FETCHED          = "Order fetched successfully";
    public static final String ORDERS_FETCHED         = "Orders fetched successfully";
    public static final String ORDER_STATUS_UPDATED   = "Order status updated successfully";
    public static final String ORDER_CANCELLED        = "Order cancelled successfully";

    // ── Payment ──────────────────────────────────────────────────────────
    public static final String PAYMENT_SUBMITTED      = "Payment submitted successfully. Awaiting verification.";
    public static final String PAYMENT_VERIFIED       = "Payment verified successfully";
    public static final String PAYMENT_REJECTED       = "Payment rejected";

    // ── Wishlist ─────────────────────────────────────────────────────────
    public static final String WISHLIST_FETCHED       = "Wishlist fetched successfully";
    public static final String WISHLIST_ITEM_ADDED    = "Added to wishlist";
    public static final String WISHLIST_ITEM_REMOVED  = "Removed from wishlist";

    // ── Review ───────────────────────────────────────────────────────────
    public static final String REVIEW_SUBMITTED       = "Review submitted successfully";
    public static final String REVIEWS_FETCHED        = "Reviews fetched successfully";
    public static final String REVIEW_DELETED         = "Review deleted successfully";

    // ── AI ───────────────────────────────────────────────────────────────
    public static final String AI_RESPONSE_SUCCESS    = "AI response generated";
    public static final String AI_SESSION_CLEARED     = "Conversation cleared successfully";
}
