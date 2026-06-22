package com.emras.user.facade;

import com.emras.user.dto.response.UserSummaryResponse;

/**
 * Public API of the User domain.
 *
 * Other domains (order, cart, ai, etc.) that need user information
 * must ONLY depend on this interface — never on UserService,
 * UserRepository, or any other internal class of the user package.
 *
 * In a future microservices migration, this interface will be implemented
 * by an HTTP/gRPC client instead of UserServiceImpl — zero changes needed
 * in any calling domain.
 */
public interface UserFacade {

    /**
     * Check whether a user with the given ID exists.
     *
     * @param userId the user's ID
     * @return true if the user exists and is active
     */
    boolean existsById(Long userId);

    /**
     * Retrieve a lightweight summary of a user (name, email).
     * Used by order confirmation emails, admin views, etc.
     *
     * @param userId the user's ID
     * @return UserSummaryResponse
     */
    UserSummaryResponse getUserSummary(Long userId);
}
