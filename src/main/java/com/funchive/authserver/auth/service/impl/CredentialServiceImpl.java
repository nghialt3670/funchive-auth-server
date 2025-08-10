package com.funchive.authserver.auth.service.impl;

import com.funchive.authserver.auth.exception.AuthenticationNotSupportedException;
import com.funchive.authserver.auth.exception.CredentialNotFoundException;
import com.funchive.authserver.auth.exception.OAuth2ProviderNotSupportedException;
import com.funchive.authserver.auth.model.entity.Account;
import com.funchive.authserver.auth.model.entity.EmailPasswordCredential;
import com.funchive.authserver.auth.model.entity.GoogleOAuth2Credential;
import com.funchive.authserver.auth.repository.CredentialRepository;
import com.funchive.authserver.auth.repository.EmailPasswordCredentialRepository;
import com.funchive.authserver.auth.repository.GoogleOAuth2CredentialRepository;
import com.funchive.authserver.auth.service.CredentialService;
import com.funchive.authserver.user.model.dto.UserCreateDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {

    private final EntityManager entityManager;

    private final CredentialRepository credentialRepository;

    private final EmailPasswordCredentialRepository emailPasswordCredentialRepository;

    private final GoogleOAuth2CredentialRepository googleOAuth2CredentialRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserCreateDto getUserCreateDto(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken token) {
            return getUserCreateDto(token);
        }

        throw new AuthenticationNotSupportedException(authentication);
    }

    @Override
    public boolean checkAccountExists(Authentication authentication) {
        if (authentication instanceof UsernamePasswordAuthenticationToken token) {
            String email = token.getName();
            return emailPasswordCredentialRepository.existsByEmail(email);
        }

        if (authentication instanceof OAuth2AuthenticationToken token) {
            String provider = token.getAuthorizedClientRegistrationId();
            OAuth2User oAuth2User = token.getPrincipal();

            switch (provider) {
                case "google" -> {
                    String sub = oAuth2User.getAttribute("sub");
                    return googleOAuth2CredentialRepository.existsBySub(sub);
                }
                default -> throw new OAuth2ProviderNotSupportedException(provider);
            }
        }

        throw new AuthenticationNotSupportedException(authentication);
    }

    @Override
    public UUID getAccountId(Authentication authentication) {
        if (authentication instanceof UsernamePasswordAuthenticationToken token) {
            String email = token.getName();
            return emailPasswordCredentialRepository.findByEmail(email)
                    .map(EmailPasswordCredential::getAccount)
                    .map(Account::getId)
                    .orElseThrow(() -> new CredentialNotFoundException(authentication));
        }

        if (authentication instanceof OAuth2AuthenticationToken token) {
            String provider = token.getAuthorizedClientRegistrationId();
            OAuth2User oAuth2User = token.getPrincipal();

            switch (provider) {
                case "google" -> {
                    String sub = oAuth2User.getAttribute("sub");
                    return googleOAuth2CredentialRepository.findBySub(sub)
                            .map(GoogleOAuth2Credential::getAccount)
                            .map(Account::getId)
                            .orElseThrow(() -> new CredentialNotFoundException(authentication));

                }
                default -> throw new OAuth2ProviderNotSupportedException(provider);
            }
        }

        throw new AuthenticationNotSupportedException(authentication);
    }

    @Override
    public UUID getAccountId(String email) {
        return emailPasswordCredentialRepository.findByEmail(email)
                .map(EmailPasswordCredential::getAccount)
                .map(Account::getId)
                .orElse(googleOAuth2CredentialRepository.findByEmail(email)
                        .map(GoogleOAuth2Credential::getAccount)
                        .map(Account::getId)
                        .orElseThrow(() -> new CredentialNotFoundException(email)));
    }

    @Override
    public void createCredential(UUID accountId, Authentication authentication) {
        if (authentication instanceof UsernamePasswordAuthenticationToken token) {
            createEmailPasswordCredential(accountId, token);
            return;
        }

        if (authentication instanceof OAuth2AuthenticationToken token) {
            createOAuth2Credential(accountId, token);
            return;
        }

        throw new AuthenticationNotSupportedException(authentication);
    }

    @Override
    public void updateCredential(Authentication authentication) {
        if (authentication instanceof UsernamePasswordAuthenticationToken token) {
            updateEmailPasswordCredential(token);
            return;
        }

        if (authentication instanceof OAuth2AuthenticationToken token) {
            updateOAuth2Credential(token);
            return;
        }

        throw new AuthenticationNotSupportedException(authentication);
    }

    private UserCreateDto getUserCreateDto(OAuth2AuthenticationToken token) {
        String provider = token.getAuthorizedClientRegistrationId();
        OAuth2User oAuth2User = token.getPrincipal();

        return switch (provider) {
            case "google" ->
                    UserCreateDto.builder()
                        .email(oAuth2User.getAttribute("email"))
                        .name(oAuth2User.getAttribute("name"))
                        .avatarUrl(oAuth2User.getAttribute("picture"))
                        .build();

            default -> throw new OAuth2ProviderNotSupportedException(provider);
        };
    }

    private void createEmailPasswordCredential(UUID accountId, UsernamePasswordAuthenticationToken token) {
        String email = token.getName();
        String password = token.getCredentials().toString();
        String passwordHash = passwordEncoder.encode(password);
        EmailPasswordCredential credential = new EmailPasswordCredential();
        credential.setAccount(entityManager.getReference(Account.class, accountId));
        credential.setEmail(email);
        credential.setPasswordHash(passwordHash);
        credentialRepository.save(credential);
    }

    private void createOAuth2Credential(UUID accountId, OAuth2AuthenticationToken token) {
        String provider = token.getAuthorizedClientRegistrationId();
        OAuth2User oAuth2User = token.getPrincipal();
        Account account = entityManager.getReference(Account.class, accountId);

        switch (provider) {
            case "google" -> {
                GoogleOAuth2Credential credential = new GoogleOAuth2Credential();
                credential.setAccount(account);
                credential.setSub(oAuth2User.getAttribute("sub"));
                credential.setName(oAuth2User.getAttribute("name"));
                credential.setGivenName(oAuth2User.getAttribute("given_name"));
                credential.setFamilyName(oAuth2User.getAttribute("family_name"));
                credential.setEmail(oAuth2User.getAttribute("email"));
                credential.setEmailVerified(Boolean.TRUE.equals(oAuth2User.getAttribute("email_verified")));
                credential.setPicture(oAuth2User.getAttribute("picture"));
                googleOAuth2CredentialRepository.save(credential);
            }

            default -> throw new OAuth2ProviderNotSupportedException(provider);
        }
    }

    private void updateEmailPasswordCredential(UsernamePasswordAuthenticationToken token) {
        String email = token.getName();
        EmailPasswordCredential credential = emailPasswordCredentialRepository.findByEmail(email)
                .orElseThrow(() -> new CredentialNotFoundException(email));
        String password = token.getCredentials().toString();
        String passwordHash = passwordEncoder.encode(password);
        credential.setPasswordHash(passwordHash);
        emailPasswordCredentialRepository.save(credential);
    }

    private void updateOAuth2Credential(OAuth2AuthenticationToken token) {
        String provider = token.getAuthorizedClientRegistrationId();
        OAuth2User oAuth2User = token.getPrincipal();

        switch (provider) {
            case "google" -> {

                GoogleOAuth2Credential credential = googleOAuth2CredentialRepository.findByEmail(
                                oAuth2User.getAttribute("email"))
                        .orElseThrow(() -> new CredentialNotFoundException(
                                Objects.requireNonNull(oAuth2User.getAttribute("email")).toString()));
                credential.setName(oAuth2User.getAttribute("name"));
                credential.setGivenName(oAuth2User.getAttribute("given_name"));
                credential.setFamilyName(oAuth2User.getAttribute("family_name"));
                credential.setEmailVerified(Boolean.TRUE.equals(oAuth2User.getAttribute("email_verified")));
                credential.setPicture(oAuth2User.getAttribute("picture"));
                googleOAuth2CredentialRepository.save(credential);
            }

            default -> throw new OAuth2ProviderNotSupportedException(provider);
        }
    }

}
