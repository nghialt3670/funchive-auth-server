package com.funchive.authserver.auth.service.impl;

import com.funchive.authserver.auth.exception.EmailPasswordCredentialNotFoundException;
import com.funchive.authserver.auth.model.entity.Account;
import com.funchive.authserver.auth.model.entity.EmailPasswordCredential;
import com.funchive.authserver.auth.repository.EmailPasswordCredentialRepository;
import com.funchive.authserver.auth.service.CredentialService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailPasswordCredentialServiceImpl implements CredentialService {

    private final EntityManager entityManager;

    private final EmailPasswordCredentialRepository credentialRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Authentication authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication.getClass());
    }

    @Override
    @Transactional
    public void createCredential(UUID accountId, Authentication authentication) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String email = token.getName();
        String password = token.getCredentials().toString();
        String passwordHash = passwordEncoder.encode(password);
        EmailPasswordCredential credential = new EmailPasswordCredential();
        Account account = entityManager.getReference(Account.class, accountId);
        credential.setAccount(account);
        credential.setEmail(email);
        credential.setPasswordHash(passwordHash);
        credentialRepository.save(credential);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAccountExists(Authentication authentication) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String email = token.getName();
        return credentialRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccount(Authentication authentication) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String email = token.getName();
        return credentialRepository.findByEmail(email)
                .map(EmailPasswordCredential::getAccount)
                .orElseThrow(() -> new EmailPasswordCredentialNotFoundException(email));
    }

    @Override
    @Transactional
    public void updateCredential(Authentication authentication) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String email = token.getName();
        EmailPasswordCredential credential = credentialRepository.findByEmail(email)
                .orElseThrow(() -> new EmailPasswordCredentialNotFoundException(email));
        String password = token.getCredentials().toString();
        String passwordHash = passwordEncoder.encode(password);
        credential.setPasswordHash(passwordHash);
        credentialRepository.save(credential);
    }

}
