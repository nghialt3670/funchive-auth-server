package com.funchive.authserver.auth.exception;

public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException() {
        super("Account already exists");
    }
}
