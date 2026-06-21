package com.emras.shared.exception;

import com.emras.shared.constant.ErrorMessages;
import org.springframework.http.HttpStatus;

// ── 404 Not Found ────────────────────────────────────────────────────────────
public class ResourceNotFoundException extends EmrasException {
    public ResourceNotFoundException(String message) {
        super(message, ErrorMessages.Code.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
