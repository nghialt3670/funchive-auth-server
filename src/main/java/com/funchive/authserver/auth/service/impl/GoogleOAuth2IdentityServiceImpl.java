package com.funchive.authserver.auth.service.impl;

import com.funchive.authserver.auth.exception.GoogleOAuth2IdentityNotFoundException;
import com.funchive.authserver.auth.model.entity.Account;
import com.funchive.authserver.auth.model.entity.GoogleOAuth2Identity;
import com.funchive.authserver.auth.repository.GoogleOAuth2IdentityRepository;
import com.funchive.authserver.auth.service.IdentityService;
import com.funchive.authserver.user.model.dto.UserCreateDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2IdentityServiceImpl implements IdentityService {

    private final EntityManager entityManager;

    private final GoogleOAuth2IdentityRepository identityRepository;

    @Override
    public boolean supports(Authentication authentication) {
        return (authentication instanceof OAuth2AuthenticationToken token) &&
                ("google".equals(token.getAuthorizedClientRegistrationId()));
    }

    @Override
    public UserCreateDto getUserCreation(Authentication authentication) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User user = token.getPrincipal();
        return UserCreateDto.builder()
                .email(user.getAttribute("email"))
                .name(user.getAttribute("name"))
                .avatarUrl(user.getAttribute("picture"))
                .build();
    }

    @Override
    @Transactional
    public void createIdentity(UUID accountId, Authentication authentication) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User user = token.getPrincipal();
        GoogleOAuth2Identity identity = new GoogleOAuth2Identity();
        Account account = entityManager.getReference(Account.class, accountId);
        identity.setAccount(account);
        identity.setSub(user.getAttribute("sub"));
        identity.setName(user.getAttribute("name"));
        identity.setGivenName(user.getAttribute("given_name"));
        identity.setFamilyName(user.getAttribute("family_name"));
        identity.setEmail(user.getAttribute("email"));
        identity.setEmailVerified(Boolean.TRUE.equals(user.getAttribute("email_verified")));
        identity.setPicture(user.getAttribute("picture"));
        identityRepository.save(identity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAccountExists(Authentication authentication) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User user = token.getPrincipal();
        String sub = user.getAttribute("sub");
        return identityRepository.existsBySub(sub);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccount(Authentication authentication) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User user = token.getPrincipal();
        String sub = user.getAttribute("sub");
        return identityRepository.findBySub(sub)
                .map(GoogleOAuth2Identity::getAccount)
                .orElseThrow(() -> new GoogleOAuth2IdentityNotFoundException(sub));
    }

    @Override
    @Transactional
    public void updateIdentity(Authentication authentication) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User user = token.getPrincipal();
        String sub = user.getAttribute("sub");
        GoogleOAuth2Identity identity = identityRepository.findBySub(sub)
                .orElseThrow(() -> new GoogleOAuth2IdentityNotFoundException(sub));
        identity.setName(user.getAttribute("name"));
        identity.setGivenName(user.getAttribute("given_name"));
        identity.setFamilyName(user.getAttribute("family_name"));
        identity.setEmail(user.getAttribute("email"));
        identity.setEmailVerified(Boolean.TRUE.equals(user.getAttribute("email_verified")));
        identity.setPicture(user.getAttribute("picture"));
        identityRepository.save(identity);
    }
}
