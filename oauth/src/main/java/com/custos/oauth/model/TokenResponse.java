package com.custos.oauth.model;

import lombok.Builder;
import lombok.Data;

/**
 * Represents an OAuth 2.1 token response.
 * This class encapsulates the response sent by the authorization server to the client.
 */
@Data
@Builder
public class TokenResponse {
    /**
     * The access token issued by the authorization server.
     * Required for successful responses.
     */
    private String accessToken;
    
    /**
     * The type of the token issued.
     * Required for successful responses. Value is typically "Bearer".
     */
    private String tokenType;
    
    /**
     * The lifetime in seconds of the access token.
     * Required for successful responses.
     */
    private Long expiresIn;
    
    /**
     * The refresh token, which can be used to obtain new access tokens.
     * Optional.
     */
    private String refreshToken;
    
    /**
     * The scope of the access token.
     * Optional.
     */
    private String scope;
    
    /**
     * The ID token, used in OpenID Connect.
     * Optional.
     */
    private String idToken;
    
    /**
     * The error code if the token request failed.
     * Required for error responses.
     */
    private String error;
    
    /**
     * A human-readable description of the error.
     * Optional for error responses.
     */
    private String errorDescription;
    
    /**
     * A URI identifying a human-readable web page with information about the error.
     * Optional for error responses.
     */
    private String errorUri;
} 