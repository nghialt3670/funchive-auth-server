package com.funchive.authserver.auth.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String userId) {
        super(String.format("Account with user ID %s not found", userId));
    }
}
