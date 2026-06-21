package com.emras.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmrasException extends RuntimeException {

    private final String    errorCode;
    private final HttpStatus httpStatus;

    public EmrasException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode  = errorCode;
        this.httpStatus = httpStatus;
    }
}
