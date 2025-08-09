package com.funchive.authserver.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain commonSecurityFilterChain(HttpSecurity security) throws Exception {
        return security
                .securityMatcher("/**")
                .cors(corsCustomizer())
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistryCustomizer())
                .formLogin(formLoginConfigurerCustomizer())
                .build();
    }

    public Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authorizationManagerRequestMatcherRegistryCustomizer() {
        return registry -> registry
                .requestMatchers("/login", "/login.html", "/css/**", "/js/**", "/images/**")
                .permitAll()
                .anyRequest()
                .authenticated();
    }

    private Customizer<FormLoginConfigurer<HttpSecurity>> formLoginConfigurerCustomizer() {
        return configurer -> configurer
                .loginPage("/login.html");
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
