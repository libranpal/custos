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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Represents an OAuth 2.1 token response from the /token endpoint.
 * 
 * This class encapsulates the response sent by the authorization server when a client
 * exchanges an authorization code for access tokens, or when using other grant types
 * like client credentials, refresh token, or password grant.
 * 
 * The response includes:
 * - access_token: The token that can be used to access protected resources
 * - token_type: The type of token, typically "Bearer"
 * - expires_in: How long the access token is valid for, in seconds
 * - refresh_token: (Optional) Token that can be used to obtain new access tokens
 * - scope: (Optional) The scope of access granted by the tokens
 * 
 * This response follows the OAuth 2.1 specification as defined in RFC 6749 Section 5.1
 * and is typically returned with a 200 OK status code and application/json content type.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {
    @JsonProperty("access_token")
    private final String accessToken;
    
    @JsonProperty("token_type")
    private final String tokenType;
    
    @JsonProperty("expires_in")
    private final Long expiresIn;
    
    @JsonProperty("refresh_token")
    private final String refreshToken;
    
    @JsonProperty("scope")
    private final String scope;
    
    public TokenResponse(
        String accessToken,
        String tokenType,
        Long expiresIn,
        String refreshToken,
        String scope
    ) {
        if (accessToken == null) {
            throw new IllegalArgumentException("Access token cannot be null");
        }
        if (tokenType == null) {
            throw new IllegalArgumentException("Token type cannot be null");
        }
        if (expiresIn != null && expiresIn <= 0) {
            throw new IllegalArgumentException("Expires in must be positive");
        }
        
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.scope = scope;
    }
    
    /**
     * Creates a builder for TokenResponse.
     * @return a new TokenResponse.Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder class for TokenResponse.
     */
    public static class Builder {
        private String accessToken;
        private String tokenType = "Bearer"; // Default token type
        private Long expiresIn;
        private String refreshToken;
        private String scope;
        
        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }
        
        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }
        
        public Builder expiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }
        
        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }
        
        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }
        
        public TokenResponse build() {
            if (accessToken == null) {
                throw new IllegalStateException("Access token is required");
            }
            if (tokenType == null) {
                throw new IllegalStateException("Token type is required");
            }
            
            return new TokenResponse(
                accessToken,
                tokenType,
                expiresIn,
                refreshToken,
                scope
            );
        }
    }
} 