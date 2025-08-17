package com.funchive.authserver.auth.service.impl;

import com.funchive.authserver.auth.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Slf4j
@Service
@ConditionalOnProperty(name = "spring.mail.enabled", havingValue = "false", matchIfMissing = true)
public class MockEmailServiceImpl implements EmailService {
    
    private final SecureRandom random = new SecureRandom();
    
    @Override
    public void sendVerificationCode(String email, String verificationCode) {
        // Mock implementation for development/testing
        log.info("🔔 [MOCK EMAIL] Sending verification code to: {}", email);
        log.info("📧 [MOCK EMAIL] Verification Code: {}", verificationCode);
        log.info("📧 [MOCK EMAIL] Subject: Email Verification - Funchive");
        log.info("📧 [MOCK EMAIL] ------------------------");
        log.info("📧 [MOCK EMAIL] Hi there!");
        log.info("📧 [MOCK EMAIL] ");
        log.info("📧 [MOCK EMAIL] Your verification code is: {}", verificationCode);
        log.info("📧 [MOCK EMAIL] ");
        log.info("📧 [MOCK EMAIL] This code will expire in 15 minutes.");
        log.info("📧 [MOCK EMAIL] ------------------------");
        
        // In development, you can copy the code from the logs
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📧 EMAIL VERIFICATION CODE: " + verificationCode);
        System.out.println("📧 TO: " + email);
        System.out.println("=".repeat(50) + "\n");
    }
    
    @Override
    public String generateVerificationCode() {
        return String.format("%06d", random.nextInt(1000000));
    }
}
