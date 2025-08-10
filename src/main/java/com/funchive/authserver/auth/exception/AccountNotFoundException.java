package com.funchive.authserver.auth.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountId) {
        super(String.format("Account with ID %s not found", accountId));
    }
}
