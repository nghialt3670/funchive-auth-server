package com.funchive.authserver.auth.security.filter;

import com.funchive.authserver.auth.service.AccountService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class EmailRegisterStepFilter extends OncePerRequestFilter {

    private final AccountService accountService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().equals("/register/email");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String email = request.getParameter("email");

        if (email == null || email.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email is required");
            return;
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, null);
        if (accountService.checkAccountExists(token)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email already exists");
        }

    }

}
