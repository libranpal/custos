package com.custos.oauth.model;

import lombok.Data;
import lombok.Builder;

/**
 * Represents an OAuth 2.1 token request.
 * This class encapsulates the parameters sent to the token endpoint.
 */
@Data
@Builder
public class TokenRequest {
    /**
     * The grant type being requested.
     * Required for all token requests.
     */
    private String grantType;
    
    /**
     * The client identifier.
     * Required for all token requests.
     */
    private String clientId;
    
    /**
     * The client secret.
     * Required for confidential clients.
     */
    private String clientSecret;
    
    /**
     * The scope of the access request.
     * Optional.
     */
    private String scope;
    
    /**
     * The authorization code received from the authorization endpoint.
     * Required for authorization code grant.
     */
    private String code;
    
    /**
     * The PKCE code verifier.
     * Required for the authorization code grant type if PKCE was used.
     */
    private String codeVerifier;
    
    /**
     * The redirect URI used in the authorization request.
     * Required if the redirect URI was included in the authorization request.
     */
    private String redirectUri;
    
    /**
     * The resource owner's username.
     * Required for password grant.
     */
    private String username;
    
    /**
     * The resource owner's password.
     * Required for password grant.
     */
    private String password;
    
    /**
     * The refresh token.
     * Required for refresh token grant.
     */
    private String refreshToken;
} 