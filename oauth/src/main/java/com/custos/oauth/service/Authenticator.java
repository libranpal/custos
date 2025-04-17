package com.custos.oauth.service;

import com.custos.oauth.exception.OAuthException;

/**
 * Interface for authenticating users.
 */
public interface Authenticator {
    
    /**
     * Authenticates a user with the given credentials.
     *
     * @param username The username
     * @param password The password
     * @return The authenticated user's subject (typically user ID)
     * @throws OAuthException if authentication fails
     */
    String authenticate(String username, String password) throws OAuthException;
} 