package com.funchive.authserver.auth.security;

import com.funchive.authserver.auth.model.entity.Account;
import com.funchive.authserver.auth.model.entity.EmailPasswordCredential;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class AccountDetails implements UserDetails {
    private final Account account;

    public AccountDetails(Account account) {
        this.account = account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return account.getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .toList();
    }

    @Override
    public String getPassword() {
        return account.getCredentials()
                .stream()
                .filter(EmailPasswordCredential.class::isInstance)
                .map(EmailPasswordCredential.class::cast)
                .map(EmailPasswordCredential::getPasswordHash)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getUsername() {
        return account.getUser().getId().toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !account.isExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !account.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return account.getCredentials()
                .stream()
                .filter(EmailPasswordCredential.class::isInstance)
                .map(EmailPasswordCredential.class::cast)
                .map(credential -> !credential.isPasswordExpired())
                .findFirst()
                .orElse(true);
    }

    @Override
    public boolean isEnabled() {
        return !account.isEnabled();
    }

}
