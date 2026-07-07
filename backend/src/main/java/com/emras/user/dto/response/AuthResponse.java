package com.emras.user.dto.response;
import lombok.Builder;
import lombok.Getter;
/**
 * Returned on login and token refresh.
 * accessToken is in the response body.
 * refreshToken is in the httpOnly cookie — NOT in this response.
 */
@Getter
@Builder
public class AuthResponse {

    private final String            accessToken;
    private final String            tokenType;
    private final UserSummaryResponse user;
    public static AuthResponse of(String accessToken, UserSummaryResponse user) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .user(user)
                .build();
    }
}