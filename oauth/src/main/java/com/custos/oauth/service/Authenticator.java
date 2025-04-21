package com.custos.oauth.service;

import com.custos.oauth.exception.OAuthException;

/**
 * Service for handling user authentication and authorization.
 */
public interface Authenticator {

    /**
     * Authenticates a user with the given credentials.
     *
     * @param username The username
     * @param password The password
     * @return The user ID if authentication succeeds
     * @throws OAuthException if authentication fails
     */
    String authenticate(String username, String password) throws OAuthException;

    /**
     * Validates that the requested scope is allowed for the user.
     *
     * @param userId The user ID
     * @param scope The scope to validate
     * @throws OAuthException if the scope is not allowed for the user
     */
    void validateScope(String userId, String scope) throws OAuthException;

    default void registerUser(String username, String password, String scope) throws OAuthException {
        // This is a placeholder - actual implementation would store the user
        // and their scope in a database or other storage
    }
} 