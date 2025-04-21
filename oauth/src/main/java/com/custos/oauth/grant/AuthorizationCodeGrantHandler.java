package com.custos.oauth.grant;

import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.model.TokenRequest;
import com.custos.oauth.model.TokenResponse;
import com.custos.oauth.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler for the Authorization Code grant type.
 * This grant type is used in the authorization code flow.
 */
@Slf4j
@RequiredArgsConstructor
public class AuthorizationCodeGrantHandler implements GrantHandler {

    private final JwtTokenService jwtTokenService;

    @Override
    public TokenResponse handle(TokenRequest request) {
        log.info("Handling authorization code grant request");
        
        try {
            // Validate authorization code
            var claimsSet = jwtTokenService.validateAuthorizationCode(
                request.getCode(),
                request.getCodeVerifier()
            );
            
            // Get user ID and scope from the code
            String userId = claimsSet.getSubject();
            String scope = claimsSet.getStringClaim("scope");
            
            // Generate access token
            String accessToken = jwtTokenService.generateAccessToken(
                request.getClientId(),
                userId,
                scope,
                3600 // 1 hour
            );
            
            // Generate refresh token
            String refreshToken = jwtTokenService.generateRefreshToken(
                request.getClientId(),
                userId,
                scope
            );
            
            // Build response
            return TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .refreshToken(refreshToken)
                .scope(scope)
                .build();
            
        } catch (OAuthException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to handle authorization code grant", e);
            throw new OAuthException("server_error", "Failed to handle authorization code grant");
        }
    }

    @Override
    public String getGrantType() {
        return "authorization_code";
    }
} 