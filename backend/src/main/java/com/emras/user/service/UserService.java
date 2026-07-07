package com.emras.user.service;
import com.emras.user.dto.request.LoginRequest;
import com.emras.user.dto.request.RegisterRequest;
import com.emras.user.dto.response.AuthResponse;
import com.emras.user.dto.response.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
public interface UserService {
    RegisterResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request, HttpServletResponse response);
    AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response);
    void logout(String email, HttpServletResponse response);
}