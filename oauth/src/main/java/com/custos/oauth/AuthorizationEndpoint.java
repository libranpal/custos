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

package com.custos.oauth;

import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.model.AuthorizationRequest;
import com.custos.oauth.model.AuthorizationResponse;
import com.custos.oauth.service.ClientRegistrationService;
import com.custos.oauth.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuth 2.1 Authorization Endpoint implementation.
 * Handles authorization requests and responses according to RFC 6749.
 */
@Slf4j
@RestController
@RequestMapping("/oauth2/authorize")
@RequiredArgsConstructor
public class AuthorizationEndpoint {

    private final ClientRegistrationService clientRegistrationService;
    private final TokenService tokenService;

    /**
     * Handles GET requests to the authorization endpoint.
     * This is the initial request from the client to start the authorization flow.
     *
     * @param request The authorization request parameters
     * @return ResponseEntity containing the authorization response
     */
    @GetMapping
    public ResponseEntity<AuthorizationResponse> handleAuthorizationRequest(AuthorizationRequest request) {
        log.info("Received authorization request: {}", request);
        
        try {
            // Validate client
            clientRegistrationService.validateClient(request.getClientId(), request.getRedirectUri());
            
            // Validate response type
            if (!"code".equals(request.getResponseType())) {
                throw new OAuthException("unsupported_response_type", 
                    "Only 'code' response type is supported");
            }
            
            // Validate scope if provided
            if (request.getScope() != null) {
                clientRegistrationService.validateScope(request.getClientId(), request.getScope());
            }
            
            // Generate authorization code
            String code = tokenService.generateAuthorizationCode(
                request.getClientId(),
                request.getRedirectUri(),
                request.getScope()
            );
            
            // Build response
            AuthorizationResponse response = AuthorizationResponse.builder()
                .code(code)
                .state(request.getState())
                .build();
            
            return ResponseEntity.ok(response);
        } catch (OAuthException e) {
            log.error("Authorization request failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(AuthorizationResponse.builder()
                    .error(e.getErrorCode())
                    .errorDescription(e.getMessage())
                    .state(request.getState())
                    .build());
        }
    }
} 