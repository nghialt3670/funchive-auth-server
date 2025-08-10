package com.funchive.authserver.auth.service.impl;

import com.funchive.authserver.auth.exception.AccountNotFoundException;
import com.funchive.authserver.auth.exception.AccountWithUserIdNotFoundException;
import com.funchive.authserver.auth.model.dto.AuthorityDetailDto;
import com.funchive.authserver.auth.model.entity.Account;
import com.funchive.authserver.auth.model.entity.Authority;
import com.funchive.authserver.auth.repository.AccountRepository;
import com.funchive.authserver.auth.security.AccountDetails;
import com.funchive.authserver.auth.service.AccountService;
import com.funchive.authserver.auth.service.AuthorityService;
import com.funchive.authserver.auth.service.CredentialService;
import com.funchive.authserver.user.model.dto.UserCreateDto;
import com.funchive.authserver.user.model.dto.UserDetailDto;
import com.funchive.authserver.user.model.entity.User;
import com.funchive.authserver.user.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserService userService;

    private final AuthorityService authorityService;

    private final CredentialService credentialService;

    private final AccountRepository accountRepository;

    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public UserDetails getAccountDetail(UUID userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountWithUserIdNotFoundException(userId.toString()));
        return new AccountDetails(account);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails getAccountDetail(Authentication authentication) {
        UUID accountId = credentialService.getAccountId(authentication);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId.toString()));
        return new AccountDetails(account);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAccountExists(Authentication authentication) {
        return credentialService.checkAccountExists(authentication);
    }

    @Override
    @Transactional
    public UserDetails createAccount(Authentication authentication) {
        UserCreateDto userCreateDto = credentialService.getUserCreateDto(authentication);
        UserDetailDto userDetailDto = userService.createUser(userCreateDto);

        Account account = new Account();

        User user = entityManager.getReference(User.class, userDetailDto.getId());
        account.setUser(user);

        AuthorityDetailDto authorityDetailDto = authorityService.checkAuthorityExists("ROLE_USER")
                ? authorityService.getAuthorityDetail("ROLE_USER")
                : authorityService.createAuthority("ROLE_USER");
        Authority authority = entityManager.getReference(Authority.class, authorityDetailDto.getId());
        account.addAuthority(authority);

        Account savedAccount = accountRepository.save(account);

        credentialService.createCredential(savedAccount.getId(), authentication);
        return new AccountDetails(savedAccount);
    }

    @Override
    @Transactional
    public UserDetails updateAccount(Authentication authentication) {
        credentialService.updateCredential(authentication);
        return getAccountDetail(authentication);
    }

}
