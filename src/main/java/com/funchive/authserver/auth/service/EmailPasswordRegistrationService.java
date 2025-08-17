package com.funchive.authserver.auth.service;

import com.funchive.authserver.auth.model.dto.EmailPasswordRegistrationDto;
import com.funchive.authserver.auth.model.dto.RegistrationStepDto;
import com.funchive.authserver.auth.model.entity.EmailPasswordRegistration;
import com.funchive.authserver.user.model.dto.UserCreateDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface EmailPasswordRegistrationService {

    UUID startRegistration(String email);

    void updateWithPassword(UUID registrationId, String password);

    void sendVerificationCode(UUID registrationId);

    boolean verifyEmail(UUID registrationId, String verificationCode);

    void updateWithUserProfile(UUID registrationId, String name, java.time.LocalDate birthDate, String avatarUrl);

    UserDetails completeRegistration(UUID registrationId);

    EmailPasswordRegistration getRegistration(UUID registrationId);

    boolean checkEmailExists(String email);

    RegistrationStepDto getCurrentStep(UUID registrationId);

    void cleanupRegistration(UUID registrationId);

}
