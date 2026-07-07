package com.emras.user.controller;
import com.emras.shared.constant.ApiConstants;
import com.emras.shared.constant.SuccessMessages;
import com.emras.shared.model.ApiResponse;
import com.emras.user.dto.request.LoginRequest;
import com.emras.user.dto.request.RegisterRequest;
import com.emras.user.dto.response.AuthResponse;
import com.emras.user.dto.response.RegisterResponse;
import com.emras.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(ApiConstants.AUTH_BASE)
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response = userService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        SuccessMessages.USER_REGISTERED, response, HttpStatus.CREATED));
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse = userService.login(request, response);
        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.LOGIN_SUCCESS, authResponse, HttpStatus.OK));
    }
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse = userService.refreshToken(request, response);
        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.TOKEN_REFRESHED, authResponse, HttpStatus.OK));
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletResponse response) {

        userService.logout(userDetails.getUsername(), response);
        return ResponseEntity.ok(
                ApiResponse.success(SuccessMessages.LOGOUT_SUCCESS, HttpStatus.OK));
    }
}