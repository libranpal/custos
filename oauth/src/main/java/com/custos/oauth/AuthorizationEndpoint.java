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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            // Validate required parameters
            if (request.getClientId() == null || request.getClientId().trim().isEmpty()) {
                throw new OAuthException("invalid_request", "client_id is required");
            }
            
            if (request.getRedirectUri() == null || request.getRedirectUri().trim().isEmpty()) {
                throw new OAuthException("invalid_request", "redirect_uri is required");
            }
            
            // Validate client
            clientRegistrationService.validateClient(request.getClientId(), request.getRedirectUri());
            
            // Validate response type
            if (!"code".equals(request.getResponseType())) {
                throw new OAuthException("unsupported_response_type", 
                    "Only 'code' response type is supported");
            }
            
            // Validate scope if provided
            if (request.getScope() != null && !request.getScope().trim().isEmpty()) {
                clientRegistrationService.validateScope(request.getClientId(), request.getScope());
            }
            
            // Check if user is authenticated
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
            
            if (!isAuthenticated) {
                // Build login URL with parameters
                StringBuilder loginUrl = new StringBuilder("/login?")
                    .append("client_id=").append(encode(request.getClientId()))
                    .append("&redirect_uri=").append(encode(request.getRedirectUri()));
                
                if (request.getState() != null && !request.getState().trim().isEmpty()) {
                    loginUrl.append("&state=").append(encode(request.getState()));
                }
                
                if (request.getScope() != null && !request.getScope().trim().isEmpty()) {
                    loginUrl.append("&scope=").append(encode(request.getScope()));
                }
                
                return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, loginUrl.toString())
                    .build();
            }
            
            // Generate authorization code
            String code = jwtTokenService.generateAuthorizationCode(
                request.getClientId(),
                authentication.getName(),
                request.getRedirectUri(),
                request.getScope(),
                request.getCodeChallenge(),
                request.getCodeChallengeMethod()
            );
            
            // Build redirect URI with authorization code
            StringBuilder redirectUri = new StringBuilder(request.getRedirectUri())
                .append("?code=").append(encode(code));
                
            if (request.getState() != null && !request.getState().trim().isEmpty()) {
                redirectUri.append("&state=").append(encode(request.getState()));
            }
            
            return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUri.toString())
                .build();
            
        } catch (OAuthException e) {
            log.error("Authorization request failed: {}", e.getMessage());
            
            // Build error redirect URI
            StringBuilder redirectUri = new StringBuilder(request.getRedirectUri())
                .append("?error=").append(encode(e.getErrorCode()))
                .append("&error_description=").append(encode(e.getMessage()));
                
            if (request.getState() != null && !request.getState().trim().isEmpty()) {
                redirectUri.append("&state=").append(encode(request.getState()));
            }
            
            return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUri.toString())
                .build();
        }
    }

    private String encode(String value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
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