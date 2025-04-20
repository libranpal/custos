package com.custos.oauth;

import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.model.ClientRegistrationRequest;
import com.custos.oauth.model.ClientRegistrationResponse;
import com.custos.oauth.service.ClientRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * OAuth 2.1 Dynamic Client Registration Endpoint implementation.
 * Handles client registration requests and responses according to RFC 7591.
 */
@Slf4j
@RestController
@RequestMapping("/oauth2/register")
@RequiredArgsConstructor
public class ClientRegistrationEndpoint {

    private final ClientRegistrationService clientRegistrationService;

    /**
     * Registers a new client dynamically.
     *
     * @param request The client registration request
     * @return ResponseEntity containing the client registration response
     */
    @PostMapping
    public ResponseEntity<ClientRegistrationResponse> registerClient(@RequestBody ClientRegistrationRequest request) {
        log.info("Received client registration request: {}", request);
        
        try {
            // Validate required fields
            validateRegistrationRequest(request);
            
            // Register the client
            ClientRegistrationResponse response = clientRegistrationService.registerClient(request);
            return ResponseEntity.ok(response);
            
        } catch (OAuthException e) {
            log.error("Client registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ClientRegistrationResponse.builder()
                    .error(e.getErrorCode())
                    .errorDescription(e.getMessage())
                    .build());
        }
    }

    /**
     * Retrieves a registered client's information.
     *
     * @param clientId The client identifier
     * @return ResponseEntity containing the client registration response
     */
    @GetMapping("/{clientId}")
    public ResponseEntity<ClientRegistrationResponse> getClient(@PathVariable String clientId) {
        log.info("Retrieving client information for: {}", clientId);
        
        try {
            ClientRegistrationResponse response = clientRegistrationService.getClient(clientId);
            return ResponseEntity.ok(response);
            
        } catch (OAuthException e) {
            log.error("Failed to retrieve client information: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ClientRegistrationResponse.builder()
                    .error(e.getErrorCode())
                    .errorDescription(e.getMessage())
                    .build());
        }
    }

    /**
     * Updates a registered client's information.
     *
     * @param clientId The client identifier
     * @param request The update request
     * @return ResponseEntity containing the updated client registration response
     */
    @PutMapping("/{clientId}")
    public ResponseEntity<ClientRegistrationResponse> updateClient(
            @PathVariable String clientId,
            @RequestBody ClientRegistrationRequest request) {
        log.info("Updating client information for: {}", clientId);
        
        try {
            // Validate required fields
            validateRegistrationRequest(request);
            
            ClientRegistrationResponse response = clientRegistrationService.updateClient(clientId, request);
            return ResponseEntity.ok(response);
            
        } catch (OAuthException e) {
            log.error("Failed to update client information: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ClientRegistrationResponse.builder()
                    .error(e.getErrorCode())
                    .errorDescription(e.getMessage())
                    .build());
        }
    }

    /**
     * Deletes a registered client.
     *
     * @param clientId The client identifier
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(@PathVariable String clientId) {
        log.info("Deleting client: {}", clientId);
        
        try {
            clientRegistrationService.deleteClient(clientId);
            return ResponseEntity.noContent().build();
            
        } catch (OAuthException e) {
            log.error("Failed to delete client: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Validates the client registration request.
     *
     * @param request The client registration request
     * @throws OAuthException if the request is invalid
     */
    private void validateRegistrationRequest(ClientRegistrationRequest request) throws OAuthException {
        if (request.getClientName() == null || request.getClientName().isEmpty()) {
            throw new OAuthException("invalid_client_metadata", "client_name is required");
        }
        
        if (request.getClientType() == null || request.getClientType().isEmpty()) {
            throw new OAuthException("invalid_client_metadata", "client_type is required");
        }
        
        if (!"public".equals(request.getClientType()) && !"confidential".equals(request.getClientType())) {
            throw new OAuthException("invalid_client_metadata", "client_type must be either 'public' or 'confidential'");
        }
        
        if (request.getGrantTypes() == null || request.getGrantTypes().length == 0) {
            throw new OAuthException("invalid_client_metadata", "grant_types is required");
        }
        
        // Validate redirect URIs if required for the grant types
        boolean requiresRedirectUri = false;
        for (String grantType : request.getGrantTypes()) {
            if ("authorization_code".equals(grantType) || "implicit".equals(grantType)) {
                requiresRedirectUri = true;
                break;
            }
        }
        
        if (requiresRedirectUri && (request.getRedirectUris() == null || request.getRedirectUris().length == 0)) {
            throw new OAuthException("invalid_client_metadata", "redirect_uris is required for authorization_code and implicit grant types");
        }
        
        // Validate response types if required
        if (request.getResponseTypes() != null && request.getResponseTypes().length > 0) {
            for (String responseType : request.getResponseTypes()) {
                if (!"code".equals(responseType) && !"token".equals(responseType)) {
                    throw new OAuthException("invalid_client_metadata", "Unsupported response_type: " + responseType);
                }
            }
        }
        
        // Validate JWT-based client authentication
        if ("confidential".equals(request.getClientType())) {
            if (request.getJwksUri() == null && request.getJwks() == null) {
                throw new OAuthException("invalid_client_metadata", "Either jwks_uri or jwks is required for confidential clients");
            }
        }
    }
} 