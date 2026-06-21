package com.emras.shared.constant;

public final class ApiConstants {

    private ApiConstants() {}

    public static final String API_PREFIX    = "/api/v1";

    // ── Auth ─────────────────────────────────────────────────────────────
    public static final String AUTH_BASE     = API_PREFIX + "/auth";
    public static final String AUTH_REGISTER = AUTH_BASE + "/register";
    public static final String AUTH_LOGIN    = AUTH_BASE + "/login";
    public static final String AUTH_REFRESH  = AUTH_BASE + "/refresh";
    public static final String AUTH_LOGOUT   = AUTH_BASE + "/logout";

    // ── Users ────────────────────────────────────────────────────────────
    public static final String USER_BASE     = API_PREFIX + "/users";

    // ── Products ─────────────────────────────────────────────────────────
    public static final String PRODUCT_BASE  = API_PREFIX + "/products";

    // ── Categories ───────────────────────────────────────────────────────
    public static final String CATEGORY_BASE = API_PREFIX + "/categories";

    // ── Cart ─────────────────────────────────────────────────────────────
    public static final String CART_BASE     = API_PREFIX + "/cart";

    // ── Orders ───────────────────────────────────────────────────────────
    public static final String ORDER_BASE    = API_PREFIX + "/orders";

    // ── Payments ─────────────────────────────────────────────────────────
    public static final String PAYMENT_BASE  = API_PREFIX + "/payments";

    // ── AI ───────────────────────────────────────────────────────────────
    public static final String AI_BASE       = API_PREFIX + "/ai";

    // ── Admin ────────────────────────────────────────────────────────────
    public static final String ADMIN_BASE    = API_PREFIX + "/admin";

    // ── Pagination defaults ──────────────────────────────────────────────
    public static final int    DEFAULT_PAGE      = 0;
    public static final int    DEFAULT_PAGE_SIZE = 12;
    public static final int    MAX_PAGE_SIZE     = 50;
    public static final String DEFAULT_SORT_BY   = "createdAt";
    public static final String DEFAULT_SORT_DIR  = "desc";
}
