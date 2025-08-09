package com.funchive.authserver.auth.exception;

import org.springframework.security.core.Authentication;

public class CredentialNotFoundException extends RuntimeException {
    public CredentialNotFoundException(Authentication authentication) {
        super(String.format("Credential with authentication type %s not found", authentication.getClass().getName()));
    }

    public CredentialNotFoundException(String email) {
        super(String.format("Credential with email %s not found", email));
    }
}
