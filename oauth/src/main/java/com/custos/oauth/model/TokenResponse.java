package com.custos.oauth.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents an OAuth token response containing access and refresh tokens.
 */
@Getter
@Builder
public class TokenResponse {
    /**
     * The access token issued by the authorization server.
     */
    private final String accessToken;

    /**
     * The type of the token issued.
     */
    private final String tokenType;

    /**
     * The lifetime in seconds of the access token.
     */
    private final Long expiresIn;

    /**
     * The refresh token, which can be used to obtain new access tokens.
     */
    private final String refreshToken;

    /**
     * The scope of the access token.
     */
    private final String scope;
} 