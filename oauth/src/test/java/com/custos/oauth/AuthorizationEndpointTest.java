package com.custos.oauth;

import com.custos.oauth.OAuthTestApplication;
import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.model.AuthorizationRequest;
import com.custos.oauth.service.Authenticator;
import com.custos.oauth.service.ClientRegistrationService;
import com.custos.oauth.service.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ContextConfiguration(classes = OAuthTestApplication.class)
@Disabled("Temporarily disabling for debugging")
class AuthorizationEndpointTest {

    @Mock
    private ClientRegistrationService clientRegistrationService;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Mock
    private Authenticator authenticator;

    private AuthorizationEndpoint authorizationEndpoint;

    @BeforeEach
    void setUp() {
        authorizationEndpoint = new AuthorizationEndpoint(
            clientRegistrationService, jwtTokenService, authenticator);
    }

    @Test
    void handleAuthorizationRequest_ValidRequest_RedirectsToLogin() {
        // Given
        AuthorizationRequest request = AuthorizationRequest.builder()
            .responseType("code")
            .clientId("test-client")
            .redirectUri("https://client.example.com/callback")
            .scope("read write")
            .state("state123")
            .codeChallenge("E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM")
            .codeChallengeMethod("S256")
            .build();

        // When
        ResponseEntity<?> response = authorizationEndpoint.handleAuthorizationRequest(request);

        // Then
        assertNotNull(response);
        assertEquals(302, response.getStatusCodeValue());
        assertTrue(response.getHeaders().getLocation().toString().contains("/login"));
        verify(clientRegistrationService).validateClient("test-client", null);
    }

    @Test
    void handleAuthorizationRequest_InvalidResponseType_ReturnsError() {
        // Given
        AuthorizationRequest request = AuthorizationRequest.builder()
            .responseType("invalid")
            .clientId("test-client")
            .redirectUri("https://client.example.com/callback")
            .build();

        // When
        ResponseEntity<?> response = authorizationEndpoint.handleAuthorizationRequest(request);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("unsupported_response_type"));
    }

    @Test
    void handleConsent_ValidRequest_RedirectsWithCode() throws Exception {
        // Given
        AuthorizationRequest request = AuthorizationRequest.builder()
            .responseType("code")
            .clientId("test-client")
            .redirectUri("https://client.example.com/callback")
            .scope("read write")
            .state("state123")
            .codeChallenge("E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM")
            .codeChallengeMethod("S256")
            .build();

        // When
        ResponseEntity<?> response = authorizationEndpoint.handleConsent(request);

        // Then
        assertNotNull(response);
        assertEquals(302, response.getStatusCodeValue());
        assertTrue(response.getHeaders().getLocation().toString().contains("code="));
        assertTrue(response.getHeaders().getLocation().toString().contains("state=state123"));
        verify(clientRegistrationService).validateClient("test-client", null);
    }

    @Test
    void handleConsent_InvalidClient_ReturnsError() {
        // Given
        AuthorizationRequest request = AuthorizationRequest.builder()
            .responseType("code")
            .clientId("invalid-client")
            .redirectUri("https://client.example.com/callback")
            .build();

        doThrow(new OAuthException("invalid_client", "Invalid client"))
            .when(clientRegistrationService).validateClient(anyString(), anyString());

        // When
        ResponseEntity<?> response = authorizationEndpoint.handleConsent(request);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("invalid_client"));
    }
} 