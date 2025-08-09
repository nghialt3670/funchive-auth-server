package com.funchive.authserver.auth.utils;

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.UUID;

public class OAuth2AuthorizationUtils {

    public static OAuth2AuthorizationRequest buildAuthorizationRequest(
            OAuth2AuthorizationCodeRequestAuthenticationToken token) {
        return OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri(token.getAuthorizationUri())
                .clientId(token.getClientId())
                .redirectUri(token.getRedirectUri())
                .scopes(token.getScopes())
                .state(token.getState())
                .additionalParameters(token.getAdditionalParameters())
                .build();
    }

    public static OAuth2AuthorizationCode buildAuthorizationCode(RegisteredClient client) {
        long tllSeconds = client.getTokenSettings().getAuthorizationCodeTimeToLive().toSeconds();
        return new OAuth2AuthorizationCode(
                UUID.randomUUID().toString(),
                Instant.now(),
                Instant.now().plusSeconds(tllSeconds)
        );
    }

    public static String buildRedirectUri(OAuth2AuthorizationRequest request, OAuth2AuthorizationCode code) {
        return UriComponentsBuilder.fromUriString(request.getRedirectUri())
                .queryParam(OAuth2ParameterNames.CODE, code.getTokenValue())
                .queryParam(OAuth2ParameterNames.STATE, request.getState())
                .build()
                .toUriString();
    }

}
