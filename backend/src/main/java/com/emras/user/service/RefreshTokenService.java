package com.emras.user.service;
import com.emras.user.model.RefreshToken;
import com.emras.user.model.User;
public interface RefreshTokenService {
    /**
     * Generate a new raw refresh token, hash it, save to DB, return the raw value.
     * The raw value goes into the httpOnly cookie — never stored anywhere else.
     */
    String createRefreshToken(User user);
    /**
     * Validate the raw token from the cookie against the DB.
     * Returns the RefreshToken entity if valid.
     * Throws exception if not found or expired.
     */
    RefreshToken validateRefreshToken(String rawToken);
    /**
     * Delete all refresh tokens for a user (logout / security reset).
     */
    void deleteByUserId(Long userId);
}