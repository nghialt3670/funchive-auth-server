package com.funchive.authserver.auth.service.impl;

import com.funchive.authserver.auth.exception.AccountAlreadyExistsException;
import com.funchive.authserver.auth.exception.AccountWithUserIdNotFoundException;
import com.funchive.authserver.auth.exception.AuthenticationNotSupportedException;
import com.funchive.authserver.auth.model.dto.AuthorityDetailDto;
import com.funchive.authserver.auth.model.entity.Account;
import com.funchive.authserver.auth.model.entity.Authority;
import com.funchive.authserver.auth.repository.AccountRepository;
import com.funchive.authserver.auth.security.AccountDetails;
import com.funchive.authserver.auth.service.AccountService;
import com.funchive.authserver.auth.service.AuthorityService;
import com.funchive.authserver.auth.service.CredentialService;
import com.funchive.authserver.auth.service.IdentityService;
import com.funchive.authserver.user.model.dto.UserCreateDto;
import com.funchive.authserver.user.model.dto.UserDetailDto;
import com.funchive.authserver.user.model.dto.UserUpdateDto;
import com.funchive.authserver.user.model.entity.User;
import com.funchive.authserver.user.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final String DEFAULT_AUTHORITY_NAME = "ROLE_USER";

    private final UserService userService;

    private final AuthorityService authorityService;

    private final List<CredentialService> credentialServices;

    private final List<IdentityService> identityServices;

    private final AccountRepository accountRepository;

    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public UserDetails getUserDetails(UUID userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountWithUserIdNotFoundException(userId.toString()));
        return new AccountDetails(account);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails getUserDetails(Authentication authentication) {
        Optional<CredentialService> credentialServiceOptional = getCredentialService(authentication);
        if (credentialServiceOptional.isPresent()) {
            CredentialService credentialService = credentialServiceOptional.get();
            Account account = credentialService.getAccount(authentication);
            return new AccountDetails(account);
        }

        Optional<IdentityService> identityServiceOptional = getIdentityService(authentication);
        if (identityServiceOptional.isPresent()) {
            IdentityService identityService = identityServiceOptional.get();
            Account account = identityService.getAccount(authentication);
            return new AccountDetails(account);
        }

        throw new AuthenticationNotSupportedException(authentication);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAccountExists(Authentication authentication) {
        for (CredentialService credentialService : credentialServices) {
            if (credentialService.supports(authentication) && credentialService.checkAccountExists(authentication)) {
                return true;
            }
        }

        for (IdentityService identityService : identityServices) {
            if (identityService.supports(authentication) && identityService.checkAccountExists(authentication)) {
                return true;
            }
        }

        return false;
    }

    @Override
    @Transactional
    public UserDetails createAccountByCredential(Authentication authentication, UserCreateDto userCreateDto) {
        Optional<CredentialService> credentialServiceOptional = getCredentialService(authentication);
        if (credentialServiceOptional.isPresent()) {
            throw new AccountAlreadyExistsException();
        }

        CredentialService credentialService = getCredentialService(authentication)
                .orElseThrow(() -> new AuthenticationNotSupportedException(authentication));
        if (credentialService.checkAccountExists(authentication)) {
            throw new AccountAlreadyExistsException();
        }

        Account account = new Account();

        User user = createUser(userCreateDto);
        account.setUser(user);

        Authority defaultAuthority = getDefaultAuthority();
        account.addAuthority(defaultAuthority);

        Account savedAccount = accountRepository.save(account);
        credentialService.createCredential(savedAccount.getId(), authentication);

        return new AccountDetails(savedAccount);
    }

    @Override
    @Transactional
    public UserDetails createAccountByIdentity(Authentication authentication) {
        Optional<CredentialService> credentialServiceOptional = getCredentialService(authentication);
        if (credentialServiceOptional.isPresent()) {
            throw new AccountAlreadyExistsException();
        }

        IdentityService identityService = getIdentityService(authentication)
                .orElseThrow(() -> new AuthenticationNotSupportedException(authentication));
        if (identityService.checkAccountExists(authentication)) {
            throw new AccountAlreadyExistsException();
        }

        Account account = new Account();

        UserCreateDto userCreateDto = identityService.getUserCreation(authentication);
        User user = createUser(userCreateDto);
        account.setUser(user);

        Authority defaultAuthority = getDefaultAuthority();
        account.addAuthority(defaultAuthority);

        Account savedAccount = accountRepository.save(account);
        identityService.createIdentity(savedAccount.getId(), authentication);

        return new AccountDetails(savedAccount);
    }

    @Override
    @Transactional
    public void updateAccountCredential(Authentication authentication, UserUpdateDto userUpdateDto) {
        CredentialService credentialService = getCredentialService(authentication)
                .orElseThrow(() -> new AuthenticationNotSupportedException(authentication));

        credentialService.updateCredential(authentication);
    }

    @Override
    @Transactional
    public void updateAccountIdentity(Authentication authentication) {
        IdentityService identityService = getIdentityService(authentication)
                .orElseThrow(() -> new AuthenticationNotSupportedException(authentication));

        identityService.updateIdentity(authentication);
    }

    private Optional<CredentialService> getCredentialService(Authentication authentication) {
        for (CredentialService credentialService : credentialServices) {
            if (credentialService.supports(authentication)) {
                return Optional.of(credentialService);
            }
        }

        return Optional.empty();
    }

    private Optional<IdentityService> getIdentityService(Authentication authentication) {
        for (IdentityService identityService : identityServices) {
            if (identityService.supports(authentication)) {
                return Optional.of(identityService);
            }
        }

        return Optional.empty();
    }

    private User createUser(UserCreateDto userCreateDto) {
        UserDetailDto userDetailDto = userService.createUser(userCreateDto);
        return entityManager.getReference(User.class, userDetailDto.getId());
    }

    private Authority getDefaultAuthority() {
        AuthorityDetailDto authorityDetailDto = authorityService.checkAuthorityExists(DEFAULT_AUTHORITY_NAME)
                ? authorityService.getAuthorityDetail(DEFAULT_AUTHORITY_NAME)
                : authorityService.createAuthority(DEFAULT_AUTHORITY_NAME);

        return entityManager.getReference(Authority.class, authorityDetailDto.getId());
    }

}
