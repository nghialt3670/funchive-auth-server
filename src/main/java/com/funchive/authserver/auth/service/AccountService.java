package com.funchive.authserver.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface AccountService {

    UserDetails getAccountDetail(UUID userId);

    UserDetails getAccountDetail(Authentication authentication);

    boolean checkAccountExists(Authentication authentication);

    UserDetails createAccount(Authentication authentication);

    UserDetails updateAccount(Authentication authentication);

}
