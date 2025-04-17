/*
 * Copyright 2024 Custos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.custos.oauth.tokens;

import lombok.Getter;

/**
 * Represents an OAuth 2.1 token request sent to the /token endpoint.
 * 
 * This class encapsulates the request parameters that a client sends to the authorization
 * server's token endpoint to obtain access tokens. The most common use case is exchanging
 * an authorization code for tokens after the user has granted permission, but it's also
 * used for other grant types.
 * 
 * The request parameters vary based on the grant type:
 * 
 * 1. Authorization Code Grant:
 *    - grant_type: "authorization_code"
 *    - client_id: The client's identifier
 *    - client_secret: (Optional) The client's secret for confidential clients
 *    - code: The authorization code received from the authorization endpoint
 * 
 * 2. Refresh Token Grant:
 *    - grant_type: "refresh_token"
 *    - client_id: The client's identifier
 *    - client_secret: (Optional) The client's secret
 *    - refresh_token: The refresh token previously received
 * 
 * 3. Client Credentials Grant:
 *    - grant_type: "client_credentials"
 *    - client_id: The client's identifier
 *    - client_secret: The client's secret
 *    - scope: (Optional) The requested scope
 * 
 * 4. Password Grant (Legacy, not recommended):
 *    - grant_type: "password"
 *    - client_id: The client's identifier
 *    - username: The resource owner's username
 *    - password: The resource owner's password
 * 
 * 5. JWT Bearer Grant:
 *    - grant_type: "urn:ietf:params:oauth:grant-type:jwt-bearer"
 *    - client_id: The client's identifier
 *    - assertion: The JWT assertion
 * 
 * The request is typically sent as a POST request with content type
 * application/x-www-form-urlencoded.
 */
@Getter
public class TokenRequest {
    private final String grantType;
    private final String clientId;
    private final String clientSecret;
    private final String username;
    private final String password;
    private final String refreshToken;
    private final String assertion;
    private final String scope;
    
    public TokenRequest(
        String grantType,
        String clientId,
        String clientSecret,
        String username,
        String password,
        String refreshToken,
        String assertion,
        String scope
    ) {
        if (grantType == null) {
            throw new IllegalArgumentException("Grant type cannot be null");
        }
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }
        
        this.grantType = grantType;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.username = username;
        this.password = password;
        this.refreshToken = refreshToken;
        this.assertion = assertion;
        this.scope = scope;
    }
    
    /**
     * Creates a builder for TokenRequest.
     * @return a new TokenRequest.Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder class for TokenRequest.
     */
    public static class Builder {
        private String grantType;
        private String clientId;
        private String clientSecret;
        private String username;
        private String password;
        private String refreshToken;
        private String assertion;
        private String scope;
        
        public Builder grantType(String grantType) {
            this.grantType = grantType;
            return this;
        }
        
        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }
        
        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }
        
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        
        public Builder password(String password) {
            this.password = password;
            return this;
        }
        
        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }
        
        public Builder assertion(String assertion) {
            this.assertion = assertion;
            return this;
        }
        
        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }
        
        public TokenRequest build() {
            if (grantType == null) {
                throw new IllegalStateException("Grant type is required");
            }
            if (clientId == null) {
                throw new IllegalStateException("Client ID is required");
            }
            
            return new TokenRequest(
                grantType,
                clientId,
                clientSecret,
                username,
                password,
                refreshToken,
                assertion,
                scope
            );
        }
    }
} 