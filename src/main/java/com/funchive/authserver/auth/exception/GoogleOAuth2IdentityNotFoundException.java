package com.funchive.authserver.auth.exception;

public class GoogleOAuth2IdentityNotFoundException extends RuntimeException {
    public GoogleOAuth2IdentityNotFoundException(String sub) {
        super(String.format("Google OAuth2 identity with sub %s not found", sub));
    }
}
