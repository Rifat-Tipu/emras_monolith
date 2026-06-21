package com.emras.shared.exception;

import com.emras.shared.constant.ErrorMessages;
import com.emras.shared.model.ApiResponse;
import com.emras.shared.util.TraceUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Domain exceptions ────────────────────────────────────────────────
    @ExceptionHandler(EmrasException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmrasException(
            EmrasException ex, HttpServletRequest request) {

        log.warn("[{}] EmrasException: {} | path={} | errorCode={}",
                TraceUtil.getTraceId(), ex.getMessage(), request.getRequestURI(), ex.getErrorCode());

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ApiResponse.failure(
                        ex.getMessage(),
                        ex.getErrorCode(),
                        ex.getHttpStatus(),
                        request.getRequestURI(),
                        TraceUtil.getTraceId()
                ));
    }

    // ── Validation errors (@Valid) ────────────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field   = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            fieldErrors.put(field, message);
        });

        log.warn("[{}] Validation failed: {} | path={}",
                TraceUtil.getTraceId(), fieldErrors, request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                        .message(ErrorMessages.VALIDATION_FAILED)
                        .errorCode(ErrorMessages.Code.VALIDATION_FAILED)
                        .data(fieldErrors)
                        .path(request.getRequestURI())
                        .traceId(TraceUtil.getTraceId())
                        .build());
    }

    // ── Spring Security: bad credentials ────────────────────────────────
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {

        log.warn("[{}] BadCredentials | path={}", TraceUtil.getTraceId(), request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failure(
                        ErrorMessages.INVALID_CREDENTIALS,
                        ErrorMessages.Code.AUTH_INVALID_CREDENTIALS,
                        HttpStatus.UNAUTHORIZED,
                        request.getRequestURI(),
                        TraceUtil.getTraceId()
                ));
    }

    // ── Spring Security: access denied ───────────────────────────────────
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        log.warn("[{}] AccessDenied | path={}", TraceUtil.getTraceId(), request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.failure(
                        ErrorMessages.ACCESS_DENIED,
                        ErrorMessages.Code.AUTH_ACCESS_DENIED,
                        HttpStatus.FORBIDDEN,
                        request.getRequestURI(),
                        TraceUtil.getTraceId()
                ));
    }

    // ── Catch-all ────────────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("[{}] Unhandled exception | path={} | message={}",
                TraceUtil.getTraceId(), request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure(
                        ErrorMessages.INTERNAL_SERVER_ERROR,
                        ErrorMessages.Code.INTERNAL_SERVER_ERROR,
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        request.getRequestURI(),
                        TraceUtil.getTraceId()
                ));
    }
}
