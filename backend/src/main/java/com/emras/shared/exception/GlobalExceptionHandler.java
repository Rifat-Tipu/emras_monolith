package com.emras.shared.exception;

import com.emras.shared.constant.ErrorMessages;
import com.emras.shared.model.ApiResponse;
import com.emras.shared.util.TraceUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Domain exceptions ─────────────────────────────────────────────────

    @ExceptionHandler(EmrasException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmrasException(
            EmrasException ex, HttpServletRequest request) {

        log.warn("[{}] EmrasException: {} | path={} | errorCode={}",
                TraceUtil.getTraceId(), ex.getMessage(),
                request.getRequestURI(), ex.getErrorCode());

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

    // ── Validation errors (@Valid) ─────────────────────────────────────────

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

    // ── Security: bad credentials ─────────────────────────────────────────

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {

        log.warn("[{}] BadCredentials | path={}",
                TraceUtil.getTraceId(), request.getRequestURI());

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

    // ── Security: account disabled ────────────────────────────────────────

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Void>> handleDisabledException(
            DisabledException ex, HttpServletRequest request) {

        log.warn("[{}] Account disabled | path={}",
                TraceUtil.getTraceId(), request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.failure(
                        "Your account has been disabled. Please contact support.",
                        ErrorMessages.Code.AUTH_ACCESS_DENIED,
                        HttpStatus.FORBIDDEN,
                        request.getRequestURI(),
                        TraceUtil.getTraceId()
                ));
    }

    // ── Security: account locked ──────────────────────────────────────────

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse<Void>> handleLockedException(
            LockedException ex, HttpServletRequest request) {

        log.warn("[{}] Account locked | path={}",
                TraceUtil.getTraceId(), request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.failure(
                        "Your account has been locked. Please contact support.",
                        ErrorMessages.Code.AUTH_ACCESS_DENIED,
                        HttpStatus.FORBIDDEN,
                        request.getRequestURI(),
                        TraceUtil.getTraceId()
                ));
    }

    // ── Security: access denied ───────────────────────────────────────────

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        log.warn("[{}] AccessDenied | path={}",
                TraceUtil.getTraceId(), request.getRequestURI());

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

    // ── DB constraint violation ───────────────────────────────────────────

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        log.error("[{}] DataIntegrityViolation | path={} | message={}",
                TraceUtil.getTraceId(), request.getRequestURI(), ex.getMessage());

        String message = "A data integrity error occurred.";

        // Give friendly messages for common constraint violations
        String cause = ex.getMostSpecificCause().getMessage().toLowerCase();
        if (cause.contains("email")) {
            message = ErrorMessages.EMAIL_ALREADY_EXISTS;
        } else if (cause.contains("sku")) {
            message = "A product with this SKU already exists.";
        } else if (cause.contains("slug")) {
            message = "A record with this slug already exists.";
        } else if (cause.contains("null") || cause.contains("not-null")) {
            message = "A required field is missing. Please check your request.";
        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.failure(
                        message,
                        ErrorMessages.Code.RESOURCE_CONFLICT,
                        HttpStatus.CONFLICT,
                        request.getRequestURI(),
                        TraceUtil.getTraceId()
                ));
    }

    // ── Wrong HTTP method ─────────────────────────────────────────────────

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        log.warn("[{}] MethodNotSupported: {} | path={}",
                TraceUtil.getTraceId(), ex.getMethod(), request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.failure(
                        "HTTP method " + ex.getMethod() + " is not supported for this endpoint.",
                        ErrorMessages.Code.INVALID_INPUT,
                        HttpStatus.METHOD_NOT_ALLOWED,
                        request.getRequestURI(),
                        TraceUtil.getTraceId()
                ));
    }

    // ── Missing request parameter ─────────────────────────────────────────

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        log.warn("[{}] MissingParam: {} | path={}",
                TraceUtil.getTraceId(), ex.getParameterName(), request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(
                        "Required parameter '" + ex.getParameterName() + "' is missing.",
                        ErrorMessages.Code.INVALID_INPUT,
                        HttpStatus.BAD_REQUEST,
                        request.getRequestURI(),
                        TraceUtil.getTraceId()
                ));
    }

    // ── Type mismatch (e.g. string passed for Long ID) ────────────────────

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        log.warn("[{}] TypeMismatch: {} | path={}",
                TraceUtil.getTraceId(), ex.getName(), request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(
                        "Invalid value for parameter '" + ex.getName() + "'.",
                        ErrorMessages.Code.INVALID_INPUT,
                        HttpStatus.BAD_REQUEST,
                        request.getRequestURI(),
                        TraceUtil.getTraceId()
                ));
    }

    // ── Unreadable request body (malformed JSON) ──────────────────────────

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableMessage(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        log.warn("[{}] UnreadableMessage | path={}",
                TraceUtil.getTraceId(), request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(
                        "Request body is missing or malformed. Please send valid JSON.",
                        ErrorMessages.Code.INVALID_INPUT,
                        HttpStatus.BAD_REQUEST,
                        request.getRequestURI(),
                        TraceUtil.getTraceId()
                ));
    }

    // ── 404 No handler found ──────────────────────────────────────────────

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFound(
            NoHandlerFoundException ex, HttpServletRequest request) {

        log.warn("[{}] NoHandlerFound: {} {} | path={}",
                TraceUtil.getTraceId(), ex.getHttpMethod(),
                ex.getRequestURL(), request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(
                        "The requested endpoint does not exist.",
                        ErrorMessages.Code.RESOURCE_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        request.getRequestURI(),
                        TraceUtil.getTraceId()
                ));
    }

    // ── Catch-all ─────────────────────────────────────────────────────────

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