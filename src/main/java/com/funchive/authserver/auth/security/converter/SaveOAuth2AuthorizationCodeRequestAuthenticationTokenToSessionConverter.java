package com.funchive.authserver.auth.security.converter;

import com.funchive.authserver.auth.constant.OAuth2AuthorizationConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeRequestAuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SaveOAuth2AuthorizationCodeRequestAuthenticationTokenToSessionConverter
        implements AuthenticationConverter {

    private static final String sessionAttributeName =
            OAuth2AuthorizationConstants.OAUTH2_AUTHORIZATION_CODE_REQUEST_AUTHENTICATION_TOKEN_SESSION_ATTRIBUTE;

    private final OAuth2AuthorizationCodeRequestAuthenticationConverter delegate =
            new OAuth2AuthorizationCodeRequestAuthenticationConverter();

    @Override
    public Authentication convert(HttpServletRequest request) {
        Authentication authentication = delegate.convert(request);

        if (authentication instanceof OAuth2AuthorizationCodeRequestAuthenticationToken token) {
            HttpSession session = request.getSession();
            session.setAttribute(sessionAttributeName, token);
        }

        return authentication;
    }
}
