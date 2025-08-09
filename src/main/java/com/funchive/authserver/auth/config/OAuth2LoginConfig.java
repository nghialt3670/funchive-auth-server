package com.funchive.authserver.auth.config;

import com.funchive.authserver.auth.security.handler.OAuth2LoginFailureHandler;
import com.funchive.authserver.auth.security.handler.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class OAuth2LoginConfig {

    private final OAuth2LoginSuccessHandler successHandler;

    private final OAuth2LoginFailureHandler failureHandler;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public SecurityFilterChain oAuth2LoginFilterChain(HttpSecurity security) throws Exception {
        return security
                .securityMatcher("/oauth2/authorization/**", "/login/oauth2/code/**")
                .sessionManagement(sessionManagementCustomizer())
                .oauth2Login(oAuth2LoginConfigurerCustomizer())
                .build();
    }

    private Customizer<SessionManagementConfigurer<HttpSecurity>> sessionManagementCustomizer() {
        return configurer -> configurer
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    }

    private Customizer<OAuth2LoginConfigurer<HttpSecurity>> oAuth2LoginConfigurerCustomizer() {
        return configurer -> configurer
                .successHandler(successHandler)
                .failureHandler(failureHandler);
    }

}
