package com.custos.oauth.grant;

import com.custos.oauth.model.TokenRequest;
import com.custos.oauth.model.TokenResponse;

/**
 * Interface for handling different OAuth 2.1 grant types.
 * Each grant type should implement this interface to handle its specific token request.
 */
public interface GrantHandler {
    
    /**
     * Handles the token request for a specific grant type.
     *
     * @param request The token request containing grant-specific parameters
     * @return TokenResponse containing the generated tokens
     */
    TokenResponse handle(TokenRequest request);
    
    /**
     * Returns the grant type that this handler supports.
     *
     * @return The grant type string (e.g., "password", "client_credentials")
     */
    String getGrantType();
} 