package com.custos.oauth.service;

import com.custos.oauth.model.ClientRegistrationRequest;
import com.custos.oauth.model.ClientRegistrationResponse;
import com.custos.oauth.exception.OAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientRegistrationServiceTest {

    @Mock
    private ClientRegistrationService clientRegistrationService;

    private static final String TEST_CLIENT_ID = "test-client";
    private static final String TEST_CLIENT_SECRET = "test-secret";
    private static final String TEST_CLIENT_NAME = "Test Client";
    private static final String[] TEST_REDIRECT_URIS = {"https://example.com/callback"};
    private static final String[] TEST_GRANT_TYPES = {"authorization_code", "refresh_token"};

    private ClientRegistrationRequest request;
    private ClientRegistrationResponse response;

    @BeforeEach
    void setUp() {
        request = ClientRegistrationRequest.builder()
                .clientName(TEST_CLIENT_NAME)
                .redirectUris(TEST_REDIRECT_URIS)
                .grantTypes(TEST_GRANT_TYPES)
                .build();

        response = ClientRegistrationResponse.builder()
                .clientId(TEST_CLIENT_ID)
                .clientSecret(TEST_CLIENT_SECRET)
                .clientName(TEST_CLIENT_NAME)
                .redirectUris(TEST_REDIRECT_URIS)
                .grantTypes(TEST_GRANT_TYPES)
                .build();
    }

    @Test
    void testRegisterClient_Success() throws OAuthException {
        when(clientRegistrationService.registerClient(any(ClientRegistrationRequest.class)))
                .thenReturn(response);

        ClientRegistrationResponse result = clientRegistrationService.registerClient(request);

        assertNotNull(result);
        assertEquals(TEST_CLIENT_ID, result.getClientId());
        assertEquals(TEST_CLIENT_SECRET, result.getClientSecret());
        assertEquals(TEST_CLIENT_NAME, result.getClientName());
        assertEquals(TEST_REDIRECT_URIS, result.getRedirectUris());
        assertEquals(TEST_GRANT_TYPES, result.getGrantTypes());

        verify(clientRegistrationService).registerClient(request);
    }

    @Test
    void testRegisterClient_Error() throws OAuthException {
        when(clientRegistrationService.registerClient(any(ClientRegistrationRequest.class)))
                .thenThrow(new OAuthException("invalid_request", "Invalid client registration request"));

        assertThrows(OAuthException.class, () -> 
            clientRegistrationService.registerClient(request)
        );

        verify(clientRegistrationService).registerClient(request);
    }

    @Test
    void testValidateClient_Success() throws OAuthException {
        doNothing().when(clientRegistrationService).validateClient(anyString(), anyString());

        assertDoesNotThrow(() -> 
            clientRegistrationService.validateClient(TEST_CLIENT_ID, "https://example.com/callback")
        );

        verify(clientRegistrationService).validateClient(TEST_CLIENT_ID, "https://example.com/callback");
    }

    @Test
    void testValidateClient_Error() throws OAuthException {
        doThrow(new OAuthException("invalid_client", "Invalid client"))
                .when(clientRegistrationService).validateClient(anyString(), anyString());

        assertThrows(OAuthException.class, () -> 
            clientRegistrationService.validateClient(TEST_CLIENT_ID, "https://example.com/callback")
        );

        verify(clientRegistrationService).validateClient(TEST_CLIENT_ID, "https://example.com/callback");
    }

    @Test
    void testValidateScope_Success() throws OAuthException {
        doNothing().when(clientRegistrationService).validateScope(anyString(), anyString());

        assertDoesNotThrow(() -> 
            clientRegistrationService.validateScope(TEST_CLIENT_ID, "read write")
        );

        verify(clientRegistrationService).validateScope(TEST_CLIENT_ID, "read write");
    }

    @Test
    void testValidateScope_Error() throws OAuthException {
        doThrow(new OAuthException("invalid_scope", "Invalid scope"))
                .when(clientRegistrationService).validateScope(anyString(), anyString());

        assertThrows(OAuthException.class, () -> 
            clientRegistrationService.validateScope(TEST_CLIENT_ID, "read write")
        );

        verify(clientRegistrationService).validateScope(TEST_CLIENT_ID, "read write");
    }

    @Test
    void testGetClient_Success() throws OAuthException {
        when(clientRegistrationService.getClient(anyString())).thenReturn(response);

        ClientRegistrationResponse result = clientRegistrationService.getClient(TEST_CLIENT_ID);

        assertNotNull(result);
        assertEquals(TEST_CLIENT_ID, result.getClientId());
        assertEquals(TEST_CLIENT_SECRET, result.getClientSecret());
        assertEquals(TEST_CLIENT_NAME, result.getClientName());

        verify(clientRegistrationService).getClient(TEST_CLIENT_ID);
    }

    @Test
    void testGetClient_NotFound() throws OAuthException {
        when(clientRegistrationService.getClient(anyString()))
                .thenThrow(new OAuthException("invalid_client", "Client not found"));

        assertThrows(OAuthException.class, () -> 
            clientRegistrationService.getClient(TEST_CLIENT_ID)
        );

        verify(clientRegistrationService).getClient(TEST_CLIENT_ID);
    }

    @Test
    void testUpdateClient_Success() throws OAuthException {
        when(clientRegistrationService.updateClient(anyString(), any(ClientRegistrationRequest.class)))
                .thenReturn(response);

        ClientRegistrationResponse result = clientRegistrationService.updateClient(TEST_CLIENT_ID, request);

        assertNotNull(result);
        assertEquals(TEST_CLIENT_ID, result.getClientId());
        assertEquals(TEST_CLIENT_SECRET, result.getClientSecret());
        assertEquals(TEST_CLIENT_NAME, result.getClientName());

        verify(clientRegistrationService).updateClient(TEST_CLIENT_ID, request);
    }

    @Test
    void testUpdateClient_NotFound() throws OAuthException {
        when(clientRegistrationService.updateClient(anyString(), any(ClientRegistrationRequest.class)))
                .thenThrow(new OAuthException("invalid_client", "Client not found"));

        assertThrows(OAuthException.class, () -> 
            clientRegistrationService.updateClient(TEST_CLIENT_ID, request)
        );

        verify(clientRegistrationService).updateClient(TEST_CLIENT_ID, request);
    }

    @Test
    void testDeleteClient_Success() throws OAuthException {
        doNothing().when(clientRegistrationService).deleteClient(anyString());

        assertDoesNotThrow(() -> 
            clientRegistrationService.deleteClient(TEST_CLIENT_ID)
        );

        verify(clientRegistrationService).deleteClient(TEST_CLIENT_ID);
    }

    @Test
    void testDeleteClient_NotFound() throws OAuthException {
        doThrow(new OAuthException("invalid_client", "Client not found"))
                .when(clientRegistrationService).deleteClient(anyString());

        assertThrows(OAuthException.class, () -> 
            clientRegistrationService.deleteClient(TEST_CLIENT_ID)
        );

        verify(clientRegistrationService).deleteClient(TEST_CLIENT_ID);
    }
} 