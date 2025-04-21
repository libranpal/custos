package com.custos.oauth.grant;

import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.model.TokenRequest;
import com.custos.oauth.model.TokenResponse;
import com.custos.oauth.service.ClientRegistrationService;
import com.custos.oauth.service.JwtTokenService;
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
    private final JwtTokenService jwtTokenService;

    @Override
    public TokenResponse handle(TokenRequest request) {
        log.info("Handling client credentials grant request for client: {}", request.getClientId());
        
        try {
            // Validate client credentials
            clientRegistrationService.validateClient(request.getClientId(), request.getClientSecret());
            
            // Validate scope if provided
            if (request.getScope() != null) {
                clientRegistrationService.validateScope(request.getClientId(), request.getScope());
            }
            
            // Generate access token
            String accessToken = jwtTokenService.generateAccessToken(
                request.getClientId(),
                request.getClientId(), // Use client ID as subject
                request.getScope(),
                3600 // 1 hour
            );
            
            // Build response
            return TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .scope(request.getScope())
                .build();
            
        } catch (OAuthException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to handle client credentials grant", e);
            throw new OAuthException("server_error", "Failed to handle client credentials grant");
        }
    }

    @Override
    public String getGrantType() {
        return "client_credentials";
    }
} 