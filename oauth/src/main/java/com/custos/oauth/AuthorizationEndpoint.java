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
import com.custos.oauth.service.Authenticator;
import com.custos.oauth.service.ClientRegistrationService;
import com.custos.oauth.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

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
    private final JwtTokenService jwtTokenService;
    private final Authenticator authenticator;

    /**
     * Handles GET requests to the authorization endpoint.
     * This is the initial request from the client to start the authorization flow.
     *
     * @param request The authorization request parameters
     * @return Redirect to login page if not authenticated, or consent page if authenticated
     */
    @GetMapping
    public ResponseEntity<?> handleAuthorizationRequest(AuthorizationRequest request) {
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
            
            // TODO: Check if user is authenticated (e.g., from session)
            boolean isAuthenticated = false;
            String userId = null;
            
            if (!isAuthenticated) {
                // Redirect to login page
                String loginUrl = String.format("/login?client_id=%s&redirect_uri=%s&state=%s&scope=%s",
                    URLEncoder.encode(request.getClientId(), StandardCharsets.UTF_8),
                    URLEncoder.encode(request.getRedirectUri(), StandardCharsets.UTF_8),
                    URLEncoder.encode(request.getState(), StandardCharsets.UTF_8),
                    URLEncoder.encode(request.getScope(), StandardCharsets.UTF_8));
                
                return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, loginUrl)
                    .build();
            }
            
            // TODO: Check if user has already consented
            boolean hasConsented = false;
            
            if (!hasConsented) {
                // Redirect to consent page
                String consentUrl = String.format("/consent?client_id=%s&redirect_uri=%s&state=%s&scope=%s",
                    URLEncoder.encode(request.getClientId(), StandardCharsets.UTF_8),
                    URLEncoder.encode(request.getRedirectUri(), StandardCharsets.UTF_8),
                    URLEncoder.encode(request.getState(), StandardCharsets.UTF_8),
                    URLEncoder.encode(request.getScope(), StandardCharsets.UTF_8));
                
                return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, consentUrl)
                    .build();
            }
            
            // Generate authorization code
            String code = jwtTokenService.generateAuthorizationCode(
                request.getClientId(),
                userId,
                request.getRedirectUri(),
                request.getScope(),
                request.getCodeChallenge(),
                request.getCodeChallengeMethod()
            );
            
            // Build redirect URI with authorization code
            String redirectUri = String.format("%s?code=%s&state=%s",
                request.getRedirectUri(),
                URLEncoder.encode(code, StandardCharsets.UTF_8),
                URLEncoder.encode(request.getState(), StandardCharsets.UTF_8));
            
            return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUri)
                .build();
            
        } catch (OAuthException e) {
            log.error("Authorization request failed: {}", e.getMessage());
            
            // Build error redirect URI
            String redirectUri = String.format("%s?error=%s&error_description=%s&state=%s",
                request.getRedirectUri(),
                URLEncoder.encode(e.getErrorCode(), StandardCharsets.UTF_8),
                URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8),
                URLEncoder.encode(request.getState(), StandardCharsets.UTF_8));
            
            return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUri)
                .build();
        }
    }

    /**
     * Handles POST requests to the consent endpoint.
     * This is called after the user has authenticated and consented to the authorization request.
     *
     * @param request The authorization request parameters
     * @return Redirect to client's redirect URI with authorization code
     */
    @PostMapping("/consent")
    public ResponseEntity<?> handleConsent(AuthorizationRequest request) {
        log.info("Received consent request: {}", request);
        
        try {
            // Validate client
            clientRegistrationService.validateClient(request.getClientId(), request.getRedirectUri());
            
            // TODO: Get authenticated user from session
            String userId = "user123"; // Replace with actual user ID
            
            // Generate authorization code
            String code = jwtTokenService.generateAuthorizationCode(
                request.getClientId(),
                userId,
                request.getRedirectUri(),
                request.getScope(),
                request.getCodeChallenge(),
                request.getCodeChallengeMethod()
            );
            
            // Build redirect URI with authorization code
            String redirectUri = String.format("%s?code=%s&state=%s",
                request.getRedirectUri(),
                URLEncoder.encode(code, StandardCharsets.UTF_8),
                URLEncoder.encode(request.getState(), StandardCharsets.UTF_8));
            
            return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUri)
                .build();
            
        } catch (OAuthException e) {
            log.error("Consent request failed: {}", e.getMessage());
            
            // Build error redirect URI
            String redirectUri = String.format("%s?error=%s&error_description=%s&state=%s",
                request.getRedirectUri(),
                URLEncoder.encode(e.getErrorCode(), StandardCharsets.UTF_8),
                URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8),
                URLEncoder.encode(request.getState(), StandardCharsets.UTF_8));
            
            return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUri)
                .build();
        }
    }
} 