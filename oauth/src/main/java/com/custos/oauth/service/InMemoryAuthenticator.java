package com.custos.oauth.service;

import com.custos.oauth.exception.OAuthException;
import org.springframework.stereotype.Service;

@Service
public class InMemoryAuthenticator implements Authenticator {

    @Override
    public String authenticate(String username, String password) throws OAuthException {
        // For demo purposes, accept any username/password
        // In a real application, this would validate against a user store
        return username;
    }

    @Override
    public void validateScope(String userId, String scope) throws OAuthException {
        // For demo purposes, accept any scope
        // In a real application, this would validate against user permissions
    }
} 