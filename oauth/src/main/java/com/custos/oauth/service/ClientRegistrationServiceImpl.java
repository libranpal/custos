package com.custos.oauth.service;

import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.model.ClientRegistrationRequest;
import com.custos.oauth.model.ClientRegistrationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of the ClientRegistrationService interface.
 */
@Slf4j
@Service
public class ClientRegistrationServiceImpl implements ClientRegistrationService {

    private final Map<String, ClientRegistrationResponse> clients = new HashMap<>();

    @Override
    public ClientRegistrationResponse registerClient(ClientRegistrationRequest request) throws OAuthException {
        log.info("Registering new client: {}", request.getClientName());
        
        // Generate client ID and secret
        String clientId = UUID.randomUUID().toString();
        String clientSecret = "confidential".equals(request.getClientType()) ? 
            UUID.randomUUID().toString() : null;
        
        // Create client registration response
        ClientRegistrationResponse response = ClientRegistrationResponse.builder()
            .clientId(clientId)
            .clientSecret(clientSecret)
            .clientName(request.getClientName())
            .clientType(request.getClientType())
            .redirectUris(request.getRedirectUris())
            .grantTypes(request.getGrantTypes())
            .responseTypes(request.getResponseTypes())
            .scopes(request.getScopes())
            .jwksUri(request.getJwksUri())
            .jwks(request.getJwks())
            .softwareStatement(request.getSoftwareStatement())
            .softwareVersion(request.getSoftwareVersion())
            .softwareId(request.getSoftwareId())
            .build();
        
        // Store client information
        clients.put(clientId, response);
        
        return response;
    }

    @Override
    public void validateClient(String clientId, String redirectUri) throws OAuthException {
        log.info("Validating client: {}", clientId);
        
        ClientRegistrationResponse client = clients.get(clientId);
        if (client == null) {
            throw new OAuthException("invalid_client", "Client not found");
        }
        
        if (redirectUri != null) {
            boolean validRedirectUri = false;
            for (String uri : client.getRedirectUris()) {
                if (uri.equals(redirectUri)) {
                    validRedirectUri = true;
                    break;
                }
            }
            
            if (!validRedirectUri) {
                throw new OAuthException("invalid_redirect_uri", "Redirect URI not registered");
            }
        }
    }

    @Override
    public void validateScope(String clientId, String scope) throws OAuthException {
        log.info("Validating scope for client: {}", clientId);
        
        ClientRegistrationResponse client = clients.get(clientId);
        if (client == null) {
            throw new OAuthException("invalid_client", "Client not found");
        }
        
        if (scope != null) {
            String[] requestedScopes = scope.split(" ");
            for (String requestedScope : requestedScopes) {
                boolean validScope = false;
                for (String allowedScope : client.getScopes()) {
                    if (allowedScope.equals(requestedScope)) {
                        validScope = true;
                        break;
                    }
                }
                
                if (!validScope) {
                    throw new OAuthException("invalid_scope", "Scope not allowed: " + requestedScope);
                }
            }
        }
    }

    @Override
    public ClientRegistrationResponse getClient(String clientId) throws OAuthException {
        log.info("Retrieving client information: {}", clientId);
        
        ClientRegistrationResponse client = clients.get(clientId);
        if (client == null) {
            throw new OAuthException("invalid_client", "Client not found");
        }
        
        return client;
    }

    @Override
    public ClientRegistrationResponse updateClient(String clientId, ClientRegistrationRequest request) throws OAuthException {
        log.info("Updating client information: {}", clientId);
        
        ClientRegistrationResponse existingClient = clients.get(clientId);
        if (existingClient == null) {
            throw new OAuthException("invalid_client", "Client not found");
        }
        
        // Create updated client registration response
        ClientRegistrationResponse response = ClientRegistrationResponse.builder()
            .clientId(clientId)
            .clientSecret(existingClient.getClientSecret())
            .clientName(request.getClientName())
            .clientType(request.getClientType())
            .redirectUris(request.getRedirectUris())
            .grantTypes(request.getGrantTypes())
            .responseTypes(request.getResponseTypes())
            .scopes(request.getScopes())
            .jwksUri(request.getJwksUri())
            .jwks(request.getJwks())
            .softwareStatement(request.getSoftwareStatement())
            .softwareVersion(request.getSoftwareVersion())
            .softwareId(request.getSoftwareId())
            .build();
        
        // Update client information
        clients.put(clientId, response);
        
        return response;
    }

    @Override
    public void deleteClient(String clientId) throws OAuthException {
        log.info("Deleting client: {}", clientId);
        
        ClientRegistrationResponse client = clients.remove(clientId);
        if (client == null) {
            throw new OAuthException("invalid_client", "Client not found");
        }
    }
} 