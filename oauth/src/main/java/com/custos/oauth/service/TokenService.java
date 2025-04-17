package com.custos.oauth.service;

import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.model.TokenResponse;

/**
 * Service for generating and managing OAuth tokens.
 */
public interface TokenService {
    
    /**
     * Generates access and refresh tokens.
     *
     * @param clientId The client identifier
     * @param subject The subject (user ID or client ID)
     * @param scope The scope of the access token
     * @return TokenResponse containing the generated tokens
     * @throws OAuthException if token generation fails
     */
    TokenResponse generateTokens(String clientId, String subject, String scope) throws OAuthException;
    
    /**
     * Generates an authorization code.
     *
     * @param clientId The client identifier
     * @param redirectUri The redirect URI
     * @param scope The scope of the authorization
     * @return The generated authorization code
     * @throws OAuthException if code generation fails
     */
    String generateAuthorizationCode(String clientId, String redirectUri, String scope) throws OAuthException;

    /**
     * Exchanges an authorization code for access and refresh tokens.
     *
     * @param code The authorization code
     * @param clientId The client identifier
     * @param redirectUri The redirect URI
     * @return The token response containing access and refresh tokens
     * @throws OAuthException if the code is invalid or exchange fails
     */
    TokenResponse exchangeAuthorizationCode(String code, String clientId, String redirectUri) throws OAuthException;

    /**
     * Refreshes an access token using a refresh token.
     *
     * @param refreshToken The refresh token
     * @param clientId The client identifier
     * @return The token response containing new access and refresh tokens
     * @throws OAuthException if the refresh token is invalid or refresh fails
     */
    TokenResponse refreshAccessToken(String refreshToken, String clientId) throws OAuthException;
} 