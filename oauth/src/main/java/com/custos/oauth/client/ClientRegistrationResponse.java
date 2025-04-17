package com.custos.oauth.client;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

/**
 * Represents a response to a client registration request.
 */
@Getter
@Builder
public class ClientRegistrationResponse {
    /**
     * The client identifier assigned by the authorization server.
     */
    private final String clientId;

    /**
     * The client secret assigned by the authorization server.
     */
    private final String clientSecret;

    /**
     * The registered client name.
     */
    private final String clientName;

    /**
     * The set of registered redirect URIs.
     */
    private final Set<String> redirectUris;

    /**
     * The set of allowed grant types.
     */
    private final Set<String> grantTypes;
} 