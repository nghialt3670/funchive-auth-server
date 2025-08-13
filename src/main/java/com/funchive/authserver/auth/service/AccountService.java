package com.funchive.authserver.auth.service;

import com.funchive.authserver.user.model.dto.UserCreateDto;
import com.funchive.authserver.user.model.dto.UserUpdateDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface AccountService {

    UserDetails getUserDetails(UUID userId);

    UserDetails getUserDetails(Authentication authentication);

    boolean checkAccountExists(Authentication authentication);

    UserDetails createAccountByCredential(Authentication authentication, UserCreateDto userCreateDto);

    UserDetails createAccountByIdentity(Authentication authentication);

    void updateAccountCredential(Authentication authentication, UserUpdateDto userUpdateDto);

    void updateAccountIdentity(Authentication authentication);

}
