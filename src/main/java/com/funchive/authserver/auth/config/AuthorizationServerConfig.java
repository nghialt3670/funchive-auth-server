package com.funchive.authserver.auth.config;

import com.funchive.authserver.auth.security.AccountDetailsService;
import com.funchive.authserver.auth.security.UserIdAuthenticationToken;
import com.funchive.authserver.user.service.UserService;
import com.funchive.authserver.user.model.dto.UserDetailDto;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2TokenEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthorizationServerConfig {

    private final AuthenticationConverter converter;
    private final UserService userService;

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

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return context -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())
                    || "id_token".equals(context.getTokenType().getValue())) {
                Authentication authentication = context.getPrincipal();
                if (authentication instanceof UserIdAuthenticationToken token) {
                    String userId = (String) token.getPrincipal();
                    context.getClaims().claim("sub", userId);

                    List<String> authorities = token.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList();
                    context.getClaims().claim("authorities", authorities);

                    UserDetailDto userDetail = userService.getUserDetailById(UUID.fromString(userId));
                    context.getClaims().claim("slug", userDetail.getSlug());
                    context.getClaims().claim("name", userDetail.getName());
                    context.getClaims().claim("email", userDetail.getEmail());
                    context.getClaims().claim("avatarUrl", userDetail.getAvatarUrl());
                }
            }
        };
    }

}
