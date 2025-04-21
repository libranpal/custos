package com.custos.oauth.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents an OAuth 2.0 token introspection response.
 * This class encapsulates the response sent by the authorization server to the client.
 */
@Getter
@Builder
public class TokenIntrospectionResponse {
    /**
     * Whether the token is active.
     * Required.
     */
    private final boolean active;
    
    /**
     * The scope of the token.
     * Optional.
     */
    private final String scope;
    
    /**
     * The client identifier.
     * Optional.
     */
    private final String clientId;
    
    /**
     * The username associated with the token.
     * Optional.
     */
    private final String username;
    
    /**
     * The type of the token.
     * Optional.
     */
    private final String tokenType;
    
    /**
     * The expiration time of the token in seconds since the epoch.
     * Optional.
     */
    private final Long exp;
    
    /**
     * The issuance time of the token in seconds since the epoch.
     * Optional.
     */
    private final Long iat;
    
    /**
     * The not-before time of the token in seconds since the epoch.
     * Optional.
     */
    private final Long nbf;
    
    /**
     * The subject of the token.
     * Optional.
     */
    private final String sub;
    
    /**
     * The audience of the token.
     * Optional.
     */
    private final String aud;
    
    /**
     * The issuer of the token.
     * Optional.
     */
    private final String iss;
    
    /**
     * The JWT ID of the token.
     * Optional.
     */
    private final String jti;
} 