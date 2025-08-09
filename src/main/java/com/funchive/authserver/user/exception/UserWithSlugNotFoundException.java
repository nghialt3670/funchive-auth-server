package com.funchive.authserver.user.exception;

public class UserWithSlugNotFoundException extends RuntimeException {
    public UserWithSlugNotFoundException(String slug) {
        super(String.format("User with slug %s not found", slug));
    }
}
