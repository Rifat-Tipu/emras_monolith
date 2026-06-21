package com.emras.shared.exception;

import org.springframework.http.HttpStatus;

// ── 409 Conflict / 400 Bad Request (business rule violations) ────────────────
public class BusinessException extends EmrasException {
    public BusinessException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.BAD_REQUEST);
    }
    public BusinessException(String message, String errorCode, HttpStatus status) {
        super(message, errorCode, status);
    }
}
