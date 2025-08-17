package com.funchive.authserver.auth.exception;

public class RegistrationNotFoundException extends RuntimeException {
    
    public RegistrationNotFoundException(String registrationId) {
        super("Registration not found with ID: " + registrationId);
    }
    
    public RegistrationNotFoundException(String registrationId, Throwable cause) {
        super("Registration not found with ID: " + registrationId, cause);
    }
}