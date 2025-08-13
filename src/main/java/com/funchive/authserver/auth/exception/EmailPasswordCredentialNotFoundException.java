package com.funchive.authserver.auth.exception;

import org.springframework.security.core.Authentication;

public class EmailPasswordCredentialNotFoundException extends RuntimeException {
    public EmailPasswordCredentialNotFoundException(String email) {
        super(String.format("Email password credential with email %s not found", email));
    }
}
