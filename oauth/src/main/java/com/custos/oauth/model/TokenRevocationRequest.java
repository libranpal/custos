package com.custos.oauth.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents an OAuth 2.0 token revocation request.
 * This class encapsulates the parameters sent by the client to the revocation endpoint.
 */
@Getter
@Builder
public class TokenRevocationRequest {
    /**
     * The token to revoke.
     * Required.
     */
    private final String token;
    
    /**
     * The type of the token.
     * Optional. Defaults to "access_token".
     */
    private final String tokenTypeHint;
    
    /**
     * The client identifier.
     * Required for confidential clients.
     */
    private final String clientId;
    
    /**
     * The client secret.
     * Required for confidential clients.
     */
    private final String clientSecret;
} 