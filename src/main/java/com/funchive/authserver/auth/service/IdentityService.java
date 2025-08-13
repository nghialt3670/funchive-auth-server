package com.funchive.authserver.auth.service;

import com.funchive.authserver.auth.model.entity.Account;
import com.funchive.authserver.user.model.dto.UserCreateDto;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface IdentityService {

    boolean supports(Authentication authentication);

    UserCreateDto getUserCreation(Authentication authentication);

    void createIdentity(UUID accountId, Authentication authentication);

    boolean checkAccountExists(Authentication authentication);

    Account getAccount(Authentication authentication);

    void updateIdentity(Authentication authentication);

}
