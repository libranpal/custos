package com.custos.oauth.service;

import com.custos.oauth.model.ClientRegistrationRequest;
import com.custos.oauth.model.ClientRegistrationResponse;
import com.custos.oauth.exception.OAuthException;

/**
 * Service for handling OAuth client registration and validation.
 */
public interface ClientRegistrationService {

    /**
     * Registers a new OAuth client.
     *
     * @param request The client registration request
     * @return The client registration response containing the client credentials
     * @throws OAuthException if registration fails
     */
    ClientRegistrationResponse registerClient(ClientRegistrationRequest request) throws OAuthException;

    /**
     * Validates a client's credentials and redirect URI.
     *
     * @param clientId The client identifier
     * @param redirectUri The redirect URI to validate
     * @throws OAuthException if the client is invalid or the redirect URI is not registered
     */
    void validateClient(String clientId, String redirectUri) throws OAuthException;

    /**
     * Validates that the requested scope is allowed for the client.
     *
     * @param clientId The client identifier
     * @param scope The scope to validate
     * @throws OAuthException if the scope is not allowed for the client
     */
    void validateScope(String clientId, String scope) throws OAuthException;

    /**
     * Retrieves the registered client information.
     *
     * @param clientId The client identifier
     * @return The client registration response
     * @throws OAuthException if the client is not found
     */
    ClientRegistrationResponse getClient(String clientId) throws OAuthException;

    /**
     * Updates an existing client's registration.
     *
     * @param clientId The client identifier
     * @param request The update request
     * @return The updated client registration response
     * @throws OAuthException if the update fails or client is not found
     */
    ClientRegistrationResponse updateClient(String clientId, ClientRegistrationRequest request) throws OAuthException;

    /**
     * Deletes a registered client.
     *
     * @param clientId The client identifier
     * @throws OAuthException if the deletion fails or client is not found
     */
    void deleteClient(String clientId) throws OAuthException;
} 