package com.custos.oauth.grant;

import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.model.TokenRequest;
import com.custos.oauth.model.TokenResponse;
import com.custos.oauth.service.Authenticator;
import com.custos.oauth.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler for the Password grant type.
 * This grant type is used when the client has the user's credentials.
 */
@Slf4j
@RequiredArgsConstructor
public class PasswordGrantHandler implements GrantHandler {

    private final Authenticator authenticator;
    private final JwtTokenService jwtTokenService;

    @Override
    public TokenResponse handle(TokenRequest request) {
        log.info("Handling password grant request for user: {}", request.getUsername());
        
        try {
            // Authenticate the user
            String userId = authenticator.authenticate(request.getUsername(), request.getPassword());
            
            // Validate scope if provided
            if (request.getScope() != null) {
                authenticator.validateScope(userId, request.getScope());
            }
            
            // Generate access token
            String accessToken = jwtTokenService.generateAccessToken(
                request.getClientId(),
                userId,
                request.getScope(),
                3600 // 1 hour
            );
            
            // Generate refresh token
            String refreshToken = jwtTokenService.generateRefreshToken(
                request.getClientId(),
                userId,
                request.getScope()
            );
            
            // Build response
            return TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .refreshToken(refreshToken)
                .scope(request.getScope())
                .build();
            
        } catch (OAuthException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to handle password grant", e);
            throw new OAuthException("server_error", "Failed to handle password grant");
        }
    }

    @Override
    public String getGrantType() {
        return "password";
    }
} 