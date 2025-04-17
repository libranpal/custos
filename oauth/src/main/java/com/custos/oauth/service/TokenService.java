package com.custos.oauth.service;

import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.model.TokenResponse;

/**
 * Service for handling OAuth token operations.
 */
public interface TokenService {

    /**
     * Generates an authorization code for the authorization code flow.
     *
     * @param clientId The client identifier
     * @param redirectUri The redirect URI
     * @param scope The requested scope
     * @return The generated authorization code
     * @throws OAuthException if token generation fails
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