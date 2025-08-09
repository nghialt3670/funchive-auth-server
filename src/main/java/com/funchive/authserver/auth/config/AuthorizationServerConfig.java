package com.funchive.authserver.auth.config;

import com.funchive.authserver.auth.security.AccountDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthorizationServerConfig {

    private final AuthenticationConverter converter;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerFilterChain(HttpSecurity security) throws Exception {
        var configurer = OAuth2AuthorizationServerConfigurer.authorizationServer();

        return security
                .securityMatcher(configurer.getEndpointsMatcher())
                .cors(corsCustomizer())
                .with(configurer, oAuth2AuthorizationServerConfigurerCustomizer())
                .build();
    }

    private Customizer<CorsConfigurer<HttpSecurity>> corsCustomizer() {
        return configurer -> configurer
                .configurationSource(request -> {
                    var config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(List.of("http://localhost:3000"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                });
    }

    private Customizer<OAuth2AuthorizationServerConfigurer> oAuth2AuthorizationServerConfigurerCustomizer() {
        return configurer -> configurer
                .oidc(Customizer.withDefaults())
                .authorizationEndpoint(authorizationEndpointCustomizer());
    }

    private Customizer<OAuth2AuthorizationEndpointConfigurer> authorizationEndpointCustomizer() {
        return configurer -> configurer
                .authorizationRequestConverter(converter);
    }

    @Bean
    public OAuth2AuthorizationService authorizationService() {
        return new InMemoryOAuth2AuthorizationService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
