package com.funchive.authserver.auth.exception;

public class OAuth2ProviderNotSupportedException extends RuntimeException {
    public OAuth2ProviderNotSupportedException(String provider) {
        super(String.format("OAuth2 provider %s is not supported", provider));
    }
}
