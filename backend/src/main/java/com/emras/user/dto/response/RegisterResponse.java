package com.emras.user.dto.response;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponse {

    private final Long   id;
    private final String fullName;
    private final String email;
    private final String message;
}