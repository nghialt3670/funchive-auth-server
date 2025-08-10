package com.funchive.authserver.auth.service;

import com.funchive.authserver.user.model.dto.UserCreateDto;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface CredentialService {

    UserCreateDto getUserCreateDto(Authentication authentication);

    boolean checkAccountExists(Authentication authentication);

    UUID getAccountId(Authentication authentication);

    UUID getAccountId(String email);

    void createCredential(UUID accountId, Authentication authentication);

    void updateCredential(Authentication authentication);

}
