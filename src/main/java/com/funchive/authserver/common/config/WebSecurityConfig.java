package com.funchive.authserver.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class WebSecurityConfig {

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public SecurityFilterChain commonSecurityFilterChain(HttpSecurity security) throws Exception {
        return security
                .securityMatcher("/**")
                .cors(corsCustomizer())
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistryCustomizer())
                .logout(logoutConfigurer())
                .build();
    }

    public Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authorizationManagerRequestMatcherRegistryCustomizer() {
        return registry -> registry
                .requestMatchers("/login", "/login.html", "/logout", "/error", "/css/**", "/js/**", "/images/**",
                        "/registration/email-password/**", "/oauth2/**")
                .permitAll()
                .anyRequest()
                .authenticated();
    }



    private Customizer<LogoutConfigurer<HttpSecurity>> logoutConfigurer() {
        return configurer -> configurer
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID", "remember-me");
    }

    private Customizer<CorsConfigurer<HttpSecurity>> corsCustomizer() {
        return configurer -> configurer
                .configurationSource(request -> {
                    var config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:3000"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                });
    }

}
