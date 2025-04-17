package com.custos.oauth.grant;

import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.model.TokenRequest;
import com.custos.oauth.model.TokenResponse;
import com.custos.oauth.service.ClientRegistrationService;
import com.custos.oauth.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler for the Client Credentials grant type.
 * This grant type is used when the client is acting on its own behalf.
 */
@Slf4j
@RequiredArgsConstructor
public class ClientCredentialsGrantHandler implements GrantHandler {

    private final ClientRegistrationService clientRegistrationService;
    private final TokenService tokenService;

    @Override
    public TokenResponse handle(TokenRequest request) {
        log.info("Handling client credentials grant request for client: {}", request.getClientId());
        
        // Validate client credentials
        clientRegistrationService.validateClient(request.getClientId(), null);
        
        // Validate scope if provided
        if (request.getScope() != null) {
            clientRegistrationService.validateScope(request.getClientId(), request.getScope());
        }
        
        // Generate tokens
        return tokenService.generateTokens(
            request.getClientId(),
            request.getClientId(), // Use client ID as subject
            request.getScope()
        );
    }

    @Override
    public String getGrantType() {
        return "client_credentials";
    }
} 