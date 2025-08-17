package com.funchive.authserver.auth.service.impl;

import com.funchive.authserver.auth.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;

@Slf4j
@Service("simpleEmailService")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.mail.enabled", havingValue = "true", matchIfMissing = false)
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final SecureRandom random = new SecureRandom();

    @Value("${spring.mail.from:noreply@funchive.com}")
    private String fromEmail;

    @Value("${app.name:Funchive}")
    private String appName;

    @Override
    public void sendVerificationCode(String email, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Email Verification - " + appName);

            // Create email content using Thymeleaf template
            Context context = new Context();
            context.setVariable("appName", appName);
            context.setVariable("verificationCode", verificationCode);
            context.setVariable("email", email);

            String htmlContent = templateEngine.process("email/verification-code", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Verification email sent successfully to: {}", email);

        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", email, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Override
    public String generateVerificationCode() {
        return String.format("%06d", random.nextInt(1000000));
    }
}
