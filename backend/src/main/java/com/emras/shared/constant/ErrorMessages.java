package com.emras.shared.constant;

public final class ErrorMessages {

    private ErrorMessages() {}

    // ── Error codes (for programmatic handling on frontend) ───────────────
    public static final class Code {
        private Code() {}

        // Auth
        public static final String AUTH_INVALID_CREDENTIALS  = "AUTH_001";
        public static final String AUTH_TOKEN_EXPIRED        = "AUTH_002";
        public static final String AUTH_TOKEN_INVALID        = "AUTH_003";
        public static final String AUTH_ACCESS_DENIED        = "AUTH_004";
        public static final String AUTH_EMAIL_ALREADY_EXISTS = "AUTH_005";
        public static final String AUTH_REFRESH_INVALID      = "AUTH_006";

        // Resource
        public static final String RESOURCE_NOT_FOUND        = "RES_001";
        public static final String RESOURCE_ALREADY_EXISTS   = "RES_002";
        public static final String RESOURCE_CONFLICT         = "RES_003";

        // Validation
        public static final String VALIDATION_FAILED         = "VAL_001";
        public static final String INVALID_INPUT             = "VAL_002";

        // Cart
        public static final String CART_ITEM_NOT_FOUND       = "CART_001";
        public static final String CART_INSUFFICIENT_STOCK   = "CART_002";
        public static final String CART_EMPTY                = "CART_003";

        // Order
        public static final String ORDER_NOT_FOUND           = "ORD_001";
        public static final String ORDER_CANNOT_CANCEL       = "ORD_002";
        public static final String ORDER_STATUS_INVALID      = "ORD_003";

        // Payment
        public static final String PAYMENT_NOT_FOUND         = "PAY_001";
        public static final String PAYMENT_ALREADY_VERIFIED  = "PAY_002";
        public static final String PROMO_CODE_INVALID        = "PAY_003";
        public static final String PROMO_CODE_EXPIRED        = "PAY_004";
        public static final String PROMO_CODE_LIMIT_REACHED  = "PAY_005";
        public static final String PROMO_MIN_ORDER_NOT_MET   = "PAY_006";

        // Product
        public static final String PRODUCT_NOT_FOUND         = "PRD_001";
        public static final String PRODUCT_OUT_OF_STOCK      = "PRD_002";
        public static final String VARIANT_NOT_FOUND         = "PRD_003";

        // Server
        public static final String INTERNAL_SERVER_ERROR     = "SRV_001";
        public static final String SERVICE_UNAVAILABLE       = "SRV_002";
    }

    // ── Human-readable messages ───────────────────────────────────────────
    public static final String INVALID_CREDENTIALS     = "Invalid email or password";
    public static final String TOKEN_EXPIRED           = "Your session has expired. Please login again";
    public static final String TOKEN_INVALID           = "Invalid or malformed token";
    public static final String ACCESS_DENIED           = "You do not have permission to access this resource";
    public static final String EMAIL_ALREADY_EXISTS    = "An account with this email already exists";
    public static final String REFRESH_TOKEN_INVALID   = "Invalid or expired refresh token";

    public static final String RESOURCE_NOT_FOUND      = "The requested resource was not found";
    public static final String USER_NOT_FOUND          = "User not found";
    public static final String PRODUCT_NOT_FOUND       = "Product not found";
    public static final String VARIANT_NOT_FOUND       = "Product variant not found";
    public static final String CATEGORY_NOT_FOUND      = "Category not found";
    public static final String ORDER_NOT_FOUND         = "Order not found";
    public static final String CART_NOT_FOUND          = "Cart not found";
    public static final String CART_ITEM_NOT_FOUND     = "Cart item not found";
    public static final String PAYMENT_NOT_FOUND       = "Payment not found";
    public static final String ADDRESS_NOT_FOUND       = "Address not found";

    public static final String INSUFFICIENT_STOCK      = "Insufficient stock for the requested quantity";
    public static final String PRODUCT_OUT_OF_STOCK    = "This product is currently out of stock";
    public static final String CART_IS_EMPTY           = "Your cart is empty";

    public static final String ORDER_CANNOT_CANCEL     = "This order can no longer be cancelled";
    public static final String PAYMENT_ALREADY_VERIFIED = "This payment has already been verified";

    public static final String PROMO_CODE_INVALID      = "Invalid promo code";
    public static final String PROMO_CODE_EXPIRED      = "This promo code has expired";
    public static final String PROMO_CODE_LIMIT_REACHED = "This promo code has reached its usage limit";
    public static final String PROMO_MIN_ORDER_NOT_MET = "Minimum order amount not met for this promo code";

    public static final String VALIDATION_FAILED       = "Validation failed. Please check the provided data";
    public static final String INTERNAL_SERVER_ERROR   = "An unexpected error occurred. Please try again later";
}
