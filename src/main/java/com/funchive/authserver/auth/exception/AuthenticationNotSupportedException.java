package com.funchive.authserver.auth.exception;

import org.springframework.security.core.Authentication;

public class AuthenticationNotSupportedException extends RuntimeException {
    public AuthenticationNotSupportedException(Authentication authentication) {
        super(String.format("Authentication of type %s is not supported", authentication.getClass().getName()));
    }
}
