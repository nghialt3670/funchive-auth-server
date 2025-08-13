package com.funchive.authserver.auth.config;

import com.funchive.authserver.auth.security.filter.EmailRegisterStepFilter;
import com.funchive.authserver.auth.security.provider.EmailPasswordRegisterProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class RegisterConfig {

    private final EmailRegisterStepFilter filter;

    private final EmailPasswordRegisterProvider provider;

    @Bean
    SecurityFilterChain registerFilterChain(HttpSecurity security) throws Exception {
        return security
                .securityMatcher("/auth/register")
//                .addFilter(filter)
                .authenticationProvider(provider)
                .build();
    }

}
