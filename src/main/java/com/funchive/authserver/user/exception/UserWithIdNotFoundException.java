package com.funchive.authserver.user.exception;

import java.util.UUID;

public class UserWithIdNotFoundException extends RuntimeException {
    public UserWithIdNotFoundException(UUID id) {
        super(String.format("User with ID %s not found", id));
    }
}
