package com.custos.oauth.client;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

/**
 * Represents a request to register a new OAuth client.
 */
@Getter
@Builder
public class ClientRegistrationRequest {
    /**
     * The client name to be registered.
     */
    private final String clientName;

    /**
     * The set of redirect URIs that the client can use.
     */
    private final Set<String> redirectUris;

    /**
     * The set of grant types that the client can use.
     */
    private final Set<String> grantTypes;
} 