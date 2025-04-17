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

package com.custos.oauth.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents an OAuth 2.1 authorization request.
 * This class encapsulates the parameters sent by the client to the authorization endpoint.
 */
@Getter
@Builder
public class AuthorizationRequest {
    /**
     * The client identifier as registered with the authorization server.
     */
    private final String clientId;
    
    /**
     * The response type requested by the client.
     * For OAuth 2.1, this must be "code" for the authorization code flow.
     */
    private final String responseType;
    
    /**
     * The URI to which the authorization server will redirect the user after authorization.
     */
    private final String redirectUri;
    
    /**
     * The scope of the access request.
     * This is optional and may be null.
     */
    private final String scope;
    
    /**
     * An opaque value used by the client to maintain state between the request and callback.
     * This is optional and may be null.
     */
    private final String state;
    
    /**
     * The code challenge used for PKCE (Proof Key for Code Exchange).
     * This is optional and may be null.
     */
    private final String codeChallenge;
    
    /**
     * The code challenge method used for PKCE.
     * This is optional and may be null.
     */
    private final String codeChallengeMethod;
} 