package com.emras.shared.util;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
public final class CookieUtil {
    private CookieUtil() {}
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";
    /**
     * Add httpOnly secure cookie carrying the refresh token.
     *
     * httpOnly  = JavaScript cannot read it (XSS protection)
     * SameSite  = Strict prevents CSRF
     * Secure    = only sent over HTTPS (set false for local dev)
     */
    public static void addRefreshTokenCookie(HttpServletResponse response,
                                             String rawToken,
                                             long maxAgeSeconds,
                                             boolean secure) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, rawToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/api/v1/auth");   // only sent to auth endpoints
        cookie.setMaxAge((int) maxAgeSeconds);
        response.addCookie(cookie);
        // Set SameSite via header (Cookie API doesn't support it directly yet)
        response.addHeader("Set-Cookie",
                String.format("%s=%s; Max-Age=%d; Path=/api/v1/auth; HttpOnly; SameSite=Strict%s",
                        REFRESH_TOKEN_COOKIE, rawToken, (int) maxAgeSeconds,
                        secure ? "; Secure" : ""));
    }
    /**
     * Clear the refresh token cookie (logout).
     */
    public static void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
    /**
     * Extract raw refresh token from the incoming request cookie.
     */
    public static Optional<String> extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) return Optional.empty();
        return Arrays.stream(request.getCookies())
                .filter(c -> REFRESH_TOKEN_COOKIE.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}