package com.custos.oauth.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents an OAuth 2.1 token request.
 * This class encapsulates the parameters sent to the token endpoint.
 */
@Getter
@Builder
public class TokenRequest {
    /**
     * The grant type being requested.
     * Required for all token requests.
     */
    private final String grantType;
    
    /**
     * The client identifier.
     * Required for all token requests.
     */
    private final String clientId;
    
    /**
     * The client secret.
     * Required for confidential clients.
     */
    private final String clientSecret;
    
    /**
     * The scope of the access request.
     * Optional.
     */
    private final String scope;
    
    /**
     * The authorization code received from the authorization endpoint.
     * Required for authorization code grant.
     */
    private final String code;
    
    /**
     * The PKCE code verifier.
     * Required for the authorization code grant type if PKCE was used.
     */
    private final String codeVerifier;
    
    /**
     * The redirect URI used in the authorization request.
     * Required if the redirect URI was included in the authorization request.
     */
    private final String redirectUri;
    
    /**
     * The resource owner's username.
     * Required for password grant.
     */
    private final String username;
    
    /**
     * The resource owner's password.
     * Required for password grant.
     */
    private final String password;
    
    /**
     * The refresh token.
     * Required for refresh token grant.
     */
    private final String refreshToken;
} 