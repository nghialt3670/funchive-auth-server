package com.funchive.authserver.auth.config;

import com.funchive.authserver.user.model.dto.UserDetailDto;
import com.funchive.authserver.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OidcConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OidcUserInfoEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthorizationServerConfig {

    private final UserService userService;

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
                .oidc(oidcConfigurerCustomizer())
                .authorizationEndpoint(authorizationEndpointCustomizer());
    }

    private Customizer<OidcConfigurer> oidcConfigurerCustomizer() {
        return configurer -> configurer
                .userInfoEndpoint(oidcUserInfoEndpointConfigurerCustomizer());
    }

    private Customizer<OidcUserInfoEndpointConfigurer> oidcUserInfoEndpointConfigurerCustomizer() {
        return configurer -> configurer
                .userInfoMapper(userInfoMapper());
    }

    private Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper() {
        return context -> {
            Authentication authentication = context.getAuthentication();
            UUID userId = UUID.fromString(authentication.getName());
            UserDetailDto userDetailDto = userService.getUserDetailById(userId);
            return new OidcUserInfo(Map.of(
                    "sub", userId.toString(),
                    "slug", userDetailDto.getSlug(),
                    "name", userDetailDto.getName(),
                    "email", userDetailDto.getEmail(),
                    "avatarUrl", userDetailDto.getAvatarUrl()));
        };
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
