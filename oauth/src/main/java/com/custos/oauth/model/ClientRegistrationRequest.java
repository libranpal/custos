package com.custos.oauth.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents an OAuth 2.1 client registration request.
 */
@Getter
@Builder
public class ClientRegistrationRequest {
    /**
     * The client name.
     * Required.
     */
    private final String clientName;
    
    /**
     * The client type.
     * Required. Must be either "public" or "confidential".
     */
    private final String clientType;
    
    /**
     * The redirect URIs.
     * Required for authorization code and implicit flows.
     */
    private final String[] redirectUris;
    
    /**
     * The grant types supported by the client.
     * Required.
     */
    private final String[] grantTypes;
    
    /**
     * The response types supported by the client.
     * Required for authorization code and implicit flows.
     */
    private final String[] responseTypes;
    
    /**
     * The scopes supported by the client.
     * Optional.
     */
    private final String[] scopes;
    
    /**
     * The client's JSON Web Key Set (JWKS) URI.
     * Required for JWT-based client authentication (RFC 7523).
     */
    private final String jwksUri;
    
    /**
     * The client's JSON Web Key Set (JWKS).
     * Alternative to jwks_uri for inline JWKS.
     */
    private final String jwks;
    
    /**
     * The client's software statement.
     * Optional. A JWT containing metadata about the client software.
     */
    private final String softwareStatement;
    
    /**
     * The client's software version.
     * Optional. Version identifier for the client software.
     */
    private final String softwareVersion;
    
    /**
     * The client's software ID.
     * Optional. Unique identifier for the client software.
     */
    private final String softwareId;

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scope;
} 