package com.funchive.authserver.auth.service.impl;

import com.funchive.authserver.auth.exception.RegistrationNotFoundException;
import com.funchive.authserver.auth.model.dto.RegistrationStepDto;
import com.funchive.authserver.auth.model.entity.EmailPasswordRegistration;
import com.funchive.authserver.auth.repository.EmailPasswordCredentialRepository;
import com.funchive.authserver.auth.repository.EmailPasswordRegistrationRepository;
import com.funchive.authserver.auth.service.AccountService;
import com.funchive.authserver.auth.service.EmailPasswordRegistrationService;
import com.funchive.authserver.auth.service.EmailService;
import com.funchive.authserver.user.model.dto.UserCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailPasswordRegistrationServiceImpl implements EmailPasswordRegistrationService {
    
    private final EmailPasswordRegistrationRepository registrationRepository;
    private final EmailPasswordCredentialRepository credentialRepository;
    private final EmailService emailService;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public UUID startRegistration(String email) {
        if (credentialRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        registrationRepository.findByEmail(email)
                .ifPresent(registrationRepository::delete);
        
        EmailPasswordRegistration registration = new EmailPasswordRegistration();
        registration.setEmail(email);
        
        EmailPasswordRegistration saved = registrationRepository.save(registration);
        log.info("Started registration for email: {}", email);
        
        return saved.getId();
    }
    
    @Override
    @Transactional
    public void updateWithPassword(UUID registrationId, String password) {
        EmailPasswordRegistration registration = getRegistration(registrationId);
        
        String hashedPassword = passwordEncoder.encode(password);
        registration.setPassword(hashedPassword);
        
        registrationRepository.save(registration);
        log.info("Updated password for registration: {}", registrationId);
    }
    
    @Override
    @Transactional
    public void sendVerificationCode(UUID registrationId) {
        EmailPasswordRegistration registration = getRegistration(registrationId);
        
        String verificationCode = emailService.generateVerificationCode();
        registration.setVerificationCode(verificationCode);
        
        registrationRepository.save(registration);
        emailService.sendVerificationCode(registration.getEmail(), verificationCode);
        
        log.info("Sent verification code for registration: {}", registrationId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean verifyEmail(UUID registrationId, String verificationCode) {
        EmailPasswordRegistration registration = getRegistration(registrationId);
        
        return registration.getVerificationCode() != null && 
               registration.getVerificationCode().equals(verificationCode);
    }
    
    @Override
    @Transactional
    public void updateWithUserProfile(UUID registrationId, String name, LocalDate birthDate, String avatarUrl) {
        EmailPasswordRegistration registration = getRegistration(registrationId);
        
        registration.setName(name);
        registration.setBirthDate(birthDate);
        registration.setAvatarUrl(avatarUrl);
        
        registrationRepository.save(registration);
        log.info("Updated profile for registration: {}", registrationId);
    }
    
    @Override
    @Transactional
    public UserDetails completeRegistration(UUID registrationId) {
        EmailPasswordRegistration registration = getRegistration(registrationId);
        
        // Validate that all required fields are set
        if (registration.getEmail() == null || registration.getPassword() == null ||
            registration.getVerificationCode() == null || registration.getName() == null) {
            throw new IllegalStateException("Registration is not complete");
        }
        
        // Create UserCreateDto from registration
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name(registration.getName())
                .email(registration.getEmail())
                .birthDate(registration.getBirthDate())
                .avatarUrl(registration.getAvatarUrl())
                .build();
        
        // Create authentication token
        UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(registration.getEmail(), registration.getPassword());
        
        // Create account
        UserDetails userDetails = accountService.createAccountByCredential(authToken, userCreateDto);
        
        // Clean up registration
        registrationRepository.delete(registration);
        
        log.info("Completed registration for email: {}", registration.getEmail());
        
        return userDetails;
    }
    
    @Override
    @Transactional(readOnly = true)
    public EmailPasswordRegistration getRegistration(UUID registrationId) {
        return registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RegistrationNotFoundException(registrationId.toString()));
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean checkEmailExists(String email) {
        return credentialRepository.existsByEmail(email) || 
               registrationRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public RegistrationStepDto getCurrentStep(UUID registrationId) {
        EmailPasswordRegistration registration = getRegistration(registrationId);
        
        boolean emailSet = registration.getEmail() != null;
        boolean passwordSet = registration.getPassword() != null;
        boolean emailVerified = registration.getVerificationCode() != null;
        boolean profileSet = registration.getName() != null;
        
        RegistrationStepDto.Step currentStep;
        if (!emailSet) {
            currentStep = RegistrationStepDto.Step.EMAIL;
        } else if (!passwordSet) {
            currentStep = RegistrationStepDto.Step.PASSWORD;
        } else if (!emailVerified) {
            currentStep = RegistrationStepDto.Step.VERIFICATION;
        } else if (!profileSet) {
            currentStep = RegistrationStepDto.Step.PROFILE;
        } else {
            currentStep = RegistrationStepDto.Step.COMPLETED;
        }
        
        return RegistrationStepDto.builder()
                .currentStep(currentStep)
                .emailSet(emailSet)
                .passwordSet(passwordSet)
                .emailVerified(emailVerified)
                .profileSet(profileSet)
                .build();
    }
    
    @Override
    @Transactional
    public void cleanupRegistration(UUID registrationId) {
        registrationRepository.deleteById(registrationId);
        log.info("Cleaned up registration: {}", registrationId);
    }
    
    @Transactional
    public void cleanupExpiredRegistrations() {
        Instant cutoffDate = Instant.now().minus(24, ChronoUnit.HOURS);
        registrationRepository.deleteExpiredRegistrations(cutoffDate);
        log.info("Cleaned up expired registrations before: {}", cutoffDate);
    }
}
