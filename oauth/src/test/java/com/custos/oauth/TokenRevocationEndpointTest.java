package com.custos.oauth;

import com.custos.oauth.OAuthTestApplication;
import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.model.TokenRevocationRequest;
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
@Disabled("Temporarily disabling for debugging")
@ContextConfiguration(classes = OAuthTestApplication.class)
class TokenRevocationEndpointTest {

    @Mock
    private ClientRegistrationService clientRegistrationService;

    @Autowired
    private JwtTokenService jwtTokenService;

    private TokenRevocationEndpoint tokenRevocationEndpoint;

    @BeforeEach
    void setUp() {
//        tokenRevocationEndpoint = new TokenRevocationEndpoint(
//            clientRegistrationService, jwtTokenService);
    }

    @Test
    void revokeToken_ValidToken_ReturnsNoContent() throws Exception {
        // Given
        String token = jwtTokenService.generateAccessToken("test-client", "user123", "read write", 3600L);
        TokenRevocationRequest request = TokenRevocationRequest.builder()
            .token(token)
            .clientId("test-client")
            .clientSecret("secret")
            .build();

        // When
        ResponseEntity<?> response = tokenRevocationEndpoint.revokeToken(request);

        // Then
        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        verify(clientRegistrationService).validateClient("test-client", "secret");
    }

    @Test
    void revokeToken_InvalidClient_ReturnsBadRequest() {
        // Given
        TokenRevocationRequest request = TokenRevocationRequest.builder()
            .token("invalid-token")
            .clientId("invalid-client")
            .clientSecret("invalid-secret")
            .build();

        doThrow(new OAuthException("invalid_client", "Invalid client"))
            .when(clientRegistrationService).validateClient(anyString(), anyString());

        // When
        ResponseEntity<?> response = tokenRevocationEndpoint.revokeToken(request);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("invalid_client"));
    }

    @Test
    void revokeToken_TokenNotOwnedByClient_ReturnsBadRequest() throws Exception {
        // Given
        String token = jwtTokenService.generateAccessToken("other-client", "user123", "read write", 3600L);
        TokenRevocationRequest request = TokenRevocationRequest.builder()
            .token(token)
            .clientId("test-client")
            .clientSecret("secret")
            .build();

        // When
        ResponseEntity<?> response = tokenRevocationEndpoint.revokeToken(request);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("invalid_request"));
    }
} 