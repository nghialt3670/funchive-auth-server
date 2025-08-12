package com.funchive.authserver.auth.security.handler;

import com.funchive.authserver.auth.constant.OAuth2AuthorizationConstants;
import com.funchive.authserver.auth.security.UserIdAuthenticationToken;
import com.funchive.authserver.auth.service.AccountService;
import com.funchive.authserver.auth.utils.OAuth2AuthorizationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizationService authorizationService;

    private final RegisteredClientRepository clientRepository;

    private final AccountService accountService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        Optional<OAuth2AuthorizationRequest> optionalOAuth2AuthorizationRequest =
                getOriginalOAuth2AuthorizationRequest(request);
        if (optionalOAuth2AuthorizationRequest.isEmpty()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST, "Original OAuth2 authorization request not found");
            return;
        }
        OAuth2AuthorizationRequest oAuth2AuthRequest = optionalOAuth2AuthorizationRequest.get();

        RegisteredClient client = clientRepository.findByClientId(oAuth2AuthRequest.getClientId());
        if (client == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown client_id");
            return;
        }

        UserDetails userDetails = accountService.checkAccountExists(authentication)
                ? accountService.updateAccount(authentication)
                : accountService.createAccount(authentication);

        Authentication principal = new UserIdAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(principal);

        OAuth2AuthorizationCode code = OAuth2AuthorizationUtils.buildAuthorizationCode(client);
        OAuth2Authorization authorization = OAuth2Authorization.withRegisteredClient(client)
                .principalName(userDetails.getUsername())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizedScopes(oAuth2AuthRequest.getScopes())
                .attribute(Principal.class.getName(), principal)
                .attribute(OAuth2AuthorizationRequest.class.getName(), oAuth2AuthRequest)
                .token(code)
                .build();
        authorizationService.save(authorization);

        String redirectUri = OAuth2AuthorizationUtils.buildRedirectUri(oAuth2AuthRequest, code);
        response.sendRedirect(redirectUri);
    }

    private Optional<OAuth2AuthorizationRequest> getOriginalOAuth2AuthorizationRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }

        Object attribute = session.getAttribute(
                OAuth2AuthorizationConstants.OAUTH2_AUTHORIZATION_CODE_REQUEST_AUTHENTICATION_TOKEN_SESSION_ATTRIBUTE);
        if (!(attribute instanceof OAuth2AuthorizationCodeRequestAuthenticationToken requestAuthToken)) {
            return Optional.empty();
        }

        OAuth2AuthorizationRequest oAuth2AuthRequest =
                OAuth2AuthorizationUtils.buildAuthorizationRequest(requestAuthToken);
        return Optional.of(oAuth2AuthRequest);
    }

}
