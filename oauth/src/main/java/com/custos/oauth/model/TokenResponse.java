package com.custos.oauth.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents an OAuth 2.1 token response.
 * This class encapsulates the parameters returned from the token endpoint.
 */
@Getter
@Builder
public class TokenResponse {
    /**
     * The access token issued by the authorization server.
     * Required for successful responses.
     */
    private final String accessToken;
    
    /**
     * The type of the token issued.
     * Required for successful responses. Value is typically "Bearer".
     */
    private final String tokenType;
    
    /**
     * The lifetime in seconds of the access token.
     * Required for successful responses.
     */
    private final Long expiresIn;
    
    /**
     * The refresh token, which can be used to obtain new access tokens.
     * Optional.
     */
    private final String refreshToken;
    
    /**
     * The scope of the access token.
     * Optional.
     */
    private final String scope;
    
    /**
     * The ID token, used in OpenID Connect.
     * Optional.
     */
    private final String idToken;
    
    /**
     * The error code if the token request failed.
     * Required for error responses.
     */
    private final String error;
    
    /**
     * A human-readable description of the error.
     * Optional for error responses.
     */
    private final String errorDescription;
    
    /**
     * A URI identifying a human-readable web page with information about the error.
     * Optional for error responses.
     */
    private final String errorUri;
} 