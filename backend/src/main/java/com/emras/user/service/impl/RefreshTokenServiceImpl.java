package com.emras.user.service.impl;
import com.emras.shared.constant.ErrorMessages;
import com.emras.shared.exception.BusinessException;
import com.emras.shared.util.TokenHashUtil;
import com.emras.user.model.RefreshToken;
import com.emras.user.model.User;
import com.emras.user.repository.RefreshTokenRepository;
import com.emras.user.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${app.jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiryMs;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    @Override
    @Transactional
    public String createRefreshToken(User user) {
        // Delete any existing token for this user (one active token per user)
        refreshTokenRepository.deleteByUserId(user.getId());
        // Generate cryptographically secure random token (256 bits)
        byte[] tokenBytes = new byte[32];
        SECURE_RANDOM.nextBytes(tokenBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        // Hash it before storing
        String tokenHash = TokenHashUtil.hash(rawToken);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiryMs))
                .build();
        refreshTokenRepository.save(refreshToken);
        log.debug("Refresh token created for userId={}", user.getId());
        // Return raw token — goes into httpOnly cookie, never into DB
        return rawToken;
    }
    @Override
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String rawToken) {
        String tokenHash = TokenHashUtil.hash(rawToken);

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BusinessException(
                        ErrorMessages.REFRESH_TOKEN_INVALID,
                        ErrorMessages.Code.AUTH_REFRESH_INVALID,
                        HttpStatus.UNAUTHORIZED
                ));
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new BusinessException(
                    ErrorMessages.REFRESH_TOKEN_INVALID,
                    ErrorMessages.Code.AUTH_REFRESH_INVALID,
                    HttpStatus.UNAUTHORIZED
            );
        }
        return refreshToken;
    }
    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.debug("Refresh tokens deleted for userId={}", userId);
    }
}