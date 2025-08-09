package com.funchive.authserver.auth.exception;

public class AuthorityNotFoundException extends RuntimeException {
    public AuthorityNotFoundException(String name) {
        super(String.format("Authority with name %s not found", name));
    }
}
