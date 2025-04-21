package com.custos.oauth.grant;

import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.model.TokenRequest;
import com.custos.oauth.model.TokenResponse;
import com.custos.oauth.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handler for the Refresh Token grant type.
 * This grant type is used to obtain a new access token using a refresh token.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RefreshTokenGrantHandler implements GrantHandler {

    private final JwtTokenService jwtTokenService;

    @Override
    public TokenResponse handle(TokenRequest request) {
        log.info("Handling refresh token grant for client: {}", request.getClientId());
        
        try {
            // Validate the refresh token
            var claimsSet = jwtTokenService.validateToken(request.getRefreshToken());
            
            // Verify the token is a refresh token
            if (!"refresh_token".equals(claimsSet.getStringClaim("token_type"))) {
                throw new OAuthException("invalid_grant", "Invalid refresh token");
            }
            
            // Verify the token belongs to the client
            String tokenClientId = claimsSet.getStringClaim("client_id");
            if (!request.getClientId().equals(tokenClientId)) {
                throw new OAuthException("invalid_client", "Token does not belong to the client");
            }
            
            // Generate new access token
            String accessToken = jwtTokenService.generateAccessToken(
                request.getClientId(),
                claimsSet.getSubject(),
                claimsSet.getStringClaim("scope"),
                3600 // 1 hour
            );
            
            // Generate new refresh token
            String refreshToken = jwtTokenService.generateRefreshToken(
                request.getClientId(),
                claimsSet.getSubject(),
                claimsSet.getStringClaim("scope")
            );
            
            // Build response
            return TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .refreshToken(refreshToken)
                .scope(claimsSet.getStringClaim("scope"))
                .build();
            
        } catch (OAuthException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to handle refresh token grant", e);
            throw new OAuthException("server_error", "Failed to handle refresh token grant");
        }
    }

    @Override
    public String getGrantType() {
        return "refresh_token";
    }
} 