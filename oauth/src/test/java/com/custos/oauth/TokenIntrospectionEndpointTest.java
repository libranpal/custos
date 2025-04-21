package com.custos.oauth;

import com.custos.oauth.config.TestConfig;
import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.model.TokenIntrospectionRequest;
import com.custos.oauth.model.TokenIntrospectionResponse;
import com.custos.oauth.service.ClientRegistrationService;
import com.custos.oauth.service.JwtTokenService;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@Disabled("Temporarily disabling for debugging")
@ContextConfiguration(classes = TestConfig.class)
class TokenIntrospectionEndpointTest {

    @Mock
    private ClientRegistrationService clientRegistrationService;

    @Autowired
    private JwtTokenService jwtTokenService;

    private TokenIntrospectionEndpoint tokenIntrospectionEndpoint;

    @BeforeEach
    void setUp() {
        tokenIntrospectionEndpoint = new TokenIntrospectionEndpoint(
            jwtTokenService, clientRegistrationService);
    }

    @Test
    void introspectToken_ValidToken_ReturnsActiveResponse() throws Exception {
        // Given
        String token = jwtTokenService.generateAccessToken("test-client", "test-user", "read write", 3600);
        TokenIntrospectionRequest request = TokenIntrospectionRequest.builder()
            .token(token)
            .clientId("test-client")
            .clientSecret("test-secret")
            .build();

        // When
        ResponseEntity<TokenIntrospectionResponse> response = 
            tokenIntrospectionEndpoint.introspectToken(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isActive());
        assertEquals("test-user", response.getBody().getUsername());
        assertEquals("test-client", response.getBody().getClientId());
        assertEquals("read write", response.getBody().getScope());
        assertEquals("Bearer", response.getBody().getTokenType());
        verify(clientRegistrationService).validateClient("test-client", "test-secret");
    }

    @Test
    void introspectToken_InvalidToken_ReturnsInactiveResponse() throws Exception {
        // Given
        TokenIntrospectionRequest request = TokenIntrospectionRequest.builder()
            .token("invalid-token")
            .clientId("test-client")
            .clientSecret("test-secret")
            .build();

        // When
        ResponseEntity<TokenIntrospectionResponse> response = 
            tokenIntrospectionEndpoint.introspectToken(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isActive());
        verify(clientRegistrationService).validateClient("test-client", "test-secret");
    }

    @Test
    void introspectToken_ExpiredToken_ReturnsInactiveResponse() throws Exception {
        // Given
        String token = jwtTokenService.generateAccessToken("test-client", "test-user", "read write", 0);
        TokenIntrospectionRequest request = TokenIntrospectionRequest.builder()
            .token(token)
            .clientId("test-client")
            .clientSecret("test-secret")
            .build();

        // When
        ResponseEntity<TokenIntrospectionResponse> response = 
            tokenIntrospectionEndpoint.introspectToken(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isActive());
        verify(clientRegistrationService).validateClient("test-client", "test-secret");
    }

    @Test
    void introspectToken_InvalidClient_ReturnsInactiveResponse() {
        // Given
        TokenIntrospectionRequest request = TokenIntrospectionRequest.builder()
            .token("valid-token")
            .clientId("invalid-client")
            .clientSecret("invalid-secret")
            .build();

        doThrow(new OAuthException("invalid_client", "Invalid client"))
            .when(clientRegistrationService).validateClient(anyString(), anyString());

        // When
        ResponseEntity<TokenIntrospectionResponse> response = 
            tokenIntrospectionEndpoint.introspectToken(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isActive());
    }
} 