package com.funchive.authserver.auth.security;

import com.funchive.authserver.auth.service.AccountService;
import com.funchive.authserver.auth.service.CredentialService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountDetailsService implements UserDetailsService {

    private final AccountService accountService;

    private final CredentialService credentialService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UUID accountId = credentialService.getAccountId(email);
        return accountService.getAccountDetail(accountId);
    }

}
