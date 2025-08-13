package com.funchive.authserver.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class EmailAlreadyExistsException extends AuthenticationException {
    public EmailAlreadyExistsException(String email) {
        super(String.format("Email %s already exists", email));
    }
}
