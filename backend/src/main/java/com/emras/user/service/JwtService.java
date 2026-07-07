package com.emras.user.service;
import org.springframework.security.core.userdetails.UserDetails;
public interface JwtService {
    /**
     * Generate a short-lived JWT access token (15 min).
     */
    String generateAccessToken(UserDetails userDetails);
    /**
     * Extract email (subject) from a JWT access token.
     */
    String extractEmail(String token);
    /**
     * Validate a JWT access token against the given user.
     */
    boolean isTokenValid(String token, UserDetails userDetails);
}