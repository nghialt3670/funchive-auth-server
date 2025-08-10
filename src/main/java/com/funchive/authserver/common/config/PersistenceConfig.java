package com.funchive.authserver.common.config;

import com.funchive.authserver.auth.model.entity.Account;
import com.funchive.authserver.common.constant.DefaultConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class PersistenceConfig {

    @Bean
    public AuditorAware<UUID> auditorAware() {
        return new AuditorAware<>() {

            @NonNull
            @Override
            public Optional<UUID> getCurrentAuditor() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Optional<UUID> defaultAuditor = Optional.of(DefaultConstants.DEFAULT_USER_ID);

                if (authentication == null || !authentication.isAuthenticated()) {
                    return defaultAuditor;
                }

                Object principal = authentication.getPrincipal();

                if (!(principal instanceof Account account)) {
                    return defaultAuditor;
                }

                return Optional.of(account.getUser().getId());
            }

        };
    }

}
