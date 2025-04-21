package com.custos.oauth.authenticators;

public interface Authenticator {
    String authenticate(String username, String password);
} 