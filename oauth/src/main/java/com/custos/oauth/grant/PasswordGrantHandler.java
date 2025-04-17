package com.custos.oauth.grant;

import com.custos.oauth.model.TokenRequest;
import com.custos.oauth.model.TokenResponse;
import com.custos.oauth.service.Authenticator;
import com.custos.oauth.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PasswordGrantHandler implements GrantHandler {
    private final Authenticator authenticator;
    private final TokenService tokenService;

    @Override
    public TokenResponse handle(TokenRequest request) {
        log.info("Handling password grant request for user: {}", request.getUsername());
        
        String subject = authenticator.authenticate(request.getUsername(), request.getPassword());
        return tokenService.generateTokens(request.getClientId(), subject, request.getScope());
    }

    @Override
    public String getGrantType() {
        return "password";
    }
} 