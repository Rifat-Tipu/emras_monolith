package com.emras.user.service.impl;
import com.emras.shared.constant.ErrorMessages;
import com.emras.shared.exception.BusinessException;
import com.emras.shared.exception.ResourceNotFoundException;
import com.emras.shared.util.CookieUtil;
import com.emras.shared.util.LogUtil;
import com.emras.user.dto.request.LoginRequest;
import com.emras.user.dto.request.RegisterRequest;
import com.emras.user.dto.response.AuthResponse;
import com.emras.user.dto.response.RegisterResponse;
import com.emras.user.dto.response.UserSummaryResponse;
import com.emras.user.facade.UserFacade;
import com.emras.user.model.RefreshToken;
import com.emras.user.model.User;
import com.emras.user.model.UserRole;
import com.emras.user.repository.UserRepository;
import com.emras.user.service.RefreshTokenService;
import com.emras.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserFacade {

    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtServiceImpl        jwtService;
    private final RefreshTokenService   refreshTokenService;
    private final AuthenticationManager authenticationManager;
    @Value("${app.jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiryMs;
    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;
    // ── Register ──────────────────────────────────────────────────────────
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        LogUtil.logServiceEntry("UserServiceImpl", "register");

        if (userRepository.existsByEmail(request.getEmail())) {
            LogUtil.logSecurityFailure("REGISTER", request.getEmail(), "email already exists");
            throw new BusinessException(
                    ErrorMessages.EMAIL_ALREADY_EXISTS,
                    ErrorMessages.Code.AUTH_EMAIL_ALREADY_EXISTS,
                    HttpStatus.CONFLICT
            );
        }
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.CUSTOMER)
                .active(true)
                .build();
        userRepository.save(user);
        LogUtil.logSecurityEvent("REGISTER_SUCCESS", user.getEmail());
        LogUtil.logBusinessEvent("USER_REGISTERED",
                "userId", user.getId(), "email", user.getEmail());
        LogUtil.logServiceExit("UserServiceImpl", "register");

        return RegisterResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .message("Account created successfully. Please login.")
                .build();
    }
    // ── Login ─────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        LogUtil.logServiceEntry("UserServiceImpl", "login");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase().trim(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        // Generate access token (JWT)
        String accessToken = jwtService.generateAccessToken(user);
        // Generate refresh token (random), save hash to DB, set in httpOnly cookie
        String rawRefreshToken = refreshTokenService.createRefreshToken(user);
        long maxAgeSecs = refreshTokenExpiryMs / 1000;
        CookieUtil.addRefreshTokenCookie(response, rawRefreshToken, maxAgeSecs, cookieSecure);
        LogUtil.logSecurityEvent("LOGIN_SUCCESS", user.getEmail());
        LogUtil.logServiceExit("UserServiceImpl", "login");
        return AuthResponse.of(accessToken, toSummary(user));
    }
    // ── Refresh Token ─────────────────────────────────────────────────────
    @Override
    @Transactional
    public AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        LogUtil.logServiceEntry("UserServiceImpl", "refreshToken");

        // Extract raw token from httpOnly cookie
        String rawToken = CookieUtil.extractRefreshToken(request)
                .orElseThrow(() -> new BusinessException(
                        ErrorMessages.REFRESH_TOKEN_INVALID,
                        ErrorMessages.Code.AUTH_REFRESH_INVALID,
                        HttpStatus.UNAUTHORIZED
                ));
        // Validate against DB
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(rawToken);
        User user = refreshToken.getUser();
        // Issue new access token
        String newAccessToken = jwtService.generateAccessToken(user);
        // Rotate refresh token (new random token, old one deleted)
        String newRawRefreshToken = refreshTokenService.createRefreshToken(user);
        long maxAgeSecs = refreshTokenExpiryMs / 1000;
        CookieUtil.addRefreshTokenCookie(response, newRawRefreshToken, maxAgeSecs, cookieSecure);
        LogUtil.logSecurityEvent("TOKEN_REFRESHED", user.getEmail());
        LogUtil.logServiceExit("UserServiceImpl", "refreshToken");
        return AuthResponse.of(newAccessToken, toSummary(user));
    }
    // ── Logout ────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void logout(String email, HttpServletResponse response) {
        userRepository.findByEmail(email).ifPresent(user -> {
            refreshTokenService.deleteByUserId(user.getId());
            LogUtil.logSecurityEvent("LOGOUT", email);
        });
        CookieUtil.clearRefreshTokenCookie(response);
    }
    // ── UserFacade impl ───────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
    @Override
    @Transactional(readOnly = true)
    public UserSummaryResponse getUserSummary(Long userId) {
        return userRepository.findById(userId)
                .map(this::toSummary)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));
    }
    // ── Helpers ───────────────────────────────────────────────────────────
    private UserSummaryResponse toSummary(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }
}