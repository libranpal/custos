package com.custos.oauth.service;

import com.custos.oauth.model.ClientRegistrationRequest;
import com.custos.oauth.model.ClientRegistrationResponse;
import com.custos.oauth.exception.OAuthException;

/**
 * Service for handling OAuth client registration and validation.
 */
public interface ClientRegistrationService {

    /**
     * Registers a new client with the authorization server.
     *
     * @param request The client registration request containing client details
     * @return The client registration response containing the client ID and other details
     * @throws OAuthException If the client registration fails
     */
    ClientRegistrationResponse registerClient(ClientRegistrationRequest request) throws OAuthException;


    ClientRegistrationResponse getClient(String clientId) throws OAuthException;

    ClientRegistrationResponse updateClient(String clientId, ClientRegistrationRequest request) throws OAuthException;

    /**
     * Validates client credentials.
     *
     * @param clientId The client identifier
     * @param clientSecret The client secret
     * @throws OAuthException If the client credentials are invalid
     */
    void validateClient(String clientId, String clientSecret) throws OAuthException;

    /**
     * Validates that the requested scope is allowed for the client.
     *
     * @param clientId The client identifier
     * @param scope The requested scope
     * @throws OAuthException If the scope is not allowed for the client
     */
    void validateScope(String clientId, String scope) throws OAuthException;

    /**
     * Deletes a client registration.
     *
     * @param clientId The client identifier
     * @throws OAuthException If the client deletion fails
     */
    void deleteClient(String clientId) throws OAuthException;
} 