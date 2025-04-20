package com.custos.oauth.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents an OAuth 2.1 client registration response.
 * This class encapsulates the response sent by the authorization server to the client.
 */
@Getter
@Builder
public class ClientRegistrationResponse {
    /**
     * The client identifier issued by the authorization server.
     */
    private final String clientId;
    
    /**
     * The client secret issued by the authorization server.
     * This is only returned for confidential clients.
     */
    private final String clientSecret;
    
    /**
     * The client name.
     */
    private final String clientName;
    
    /**
     * The client type.
     * Must be either "public" or "confidential".
     */
    private final String clientType;
    
    /**
     * The redirect URIs.
     */
    private final String[] redirectUris;
    
    /**
     * The grant types supported by the client.
     */
    private final String[] grantTypes;
    
    /**
     * The response types supported by the client.
     */
    private final String[] responseTypes;
    
    /**
     * The scopes supported by the client.
     */
    private final String[] scopes;
    
    /**
     * The client's JSON Web Key Set (JWKS) URI.
     */
    private final String jwksUri;
    
    /**
     * The client's JSON Web Key Set (JWKS).
     */
    private final String jwks;
    
    /**
     * The client's software statement.
     */
    private final String softwareStatement;
    
    /**
     * The client's software version.
     */
    private final String softwareVersion;
    
    /**
     * The client's software ID.
     */
    private final String softwareId;
    
    /**
     * The error code if registration failed.
     */
    private final String error;
    
    /**
     * The error description if registration failed.
     */
    private final String errorDescription;
} 