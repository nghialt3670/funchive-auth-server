package com.funchive.authserver.auth.service;

public interface EmailService {

    void sendVerificationCode(String email, String verificationCode);

    String generateVerificationCode();

}
