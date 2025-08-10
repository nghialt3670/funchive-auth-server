package com.funchive.authserver.auth.exception;

public class AccountWithUserIdNotFoundException extends RuntimeException {
    public AccountWithUserIdNotFoundException(String userId) {
        super(String.format("Account with user ID %s not found", userId));
    }
}
