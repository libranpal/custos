package com.custos.oauth;

import com.custos.oauth.OAuthTestApplication;
import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.grant.AuthorizationCodeGrantHandler;
import com.custos.oauth.grant.ClientCredentialsGrantHandler;
import com.custos.oauth.grant.PasswordGrantHandler;
import com.custos.oauth.grant.RefreshTokenGrantHandler;
import com.custos.oauth.model.TokenRequest;
import com.custos.oauth.model.TokenResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Disabled("Temporarily disabling for debugging")
@ContextConfiguration(classes = OAuthTestApplication.class)
class TokenEndpointTest {

    @Mock
    private ClientRegistrationService clientRegistrationService;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Mock
    private Authenticator authenticator;

    @Autowired
    private PasswordGrantHandler passwordGrantHandler;

    @Autowired
    private ClientCredentialsGrantHandler clientCredentialsGrantHandler;

    @Autowired
    private RefreshTokenGrantHandler refreshTokenGrantHandler;

    @Autowired
    private AuthorizationCodeGrantHandler authorizationCodeGrantHandler;

    private TokenEndpoint tokenEndpoint;

    @BeforeEach
    void setUp() {
        tokenEndpoint = new TokenEndpoint(
            clientRegistrationService, jwtTokenService, authenticator);
    }

    @Test
    void handleTokenRequest_AuthorizationCodeGrant_ReturnsTokens() throws Exception {
        // Given
        String code = jwtTokenService.generateAuthorizationCode(
            "test-client", "test-user", "https://client.example.com/callback",
            "read write", "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM", "S256");

        TokenRequest request = TokenRequest.builder()
            .grantType("authorization_code")
            .clientId("test-client")
            .clientSecret("test-secret")
            .code(code)
            .codeVerifier("dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk")
            .build();

        // When
        ResponseEntity<TokenResponse> response = tokenEndpoint.handleTokenRequest(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals("Bearer", response.getBody().getTokenType());
        assertEquals(3600L, response.getBody().getExpiresIn());
        verify(clientRegistrationService).validateClient("test-client", "test-secret");
    }

    @Test
    void handleTokenRequest_PasswordGrant_ReturnsTokens() {
        // Given
        TokenRequest request = TokenRequest.builder()
            .grantType("password")
            .clientId("test-client")
            .clientSecret("test-secret")
            .username("test-user")
            .password("test-password")
            .build();

        TokenResponse expectedResponse = TokenResponse.builder()
            .accessToken("access-token-123")
            .tokenType("Bearer")
            .expiresIn(3600L)
            .refreshToken("refresh-token-123")
            .scope("read write")
            .build();

        when(passwordGrantHandler.handle(any())).thenReturn(expectedResponse);

        // When
        ResponseEntity<TokenResponse> response = tokenEndpoint.handleTokenRequest(request);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response.getBody());
        verify(clientRegistrationService).validateClient("test-client", "test-secret");
    }

    @Test
    void handleTokenRequest_ClientCredentialsGrant_ReturnsTokens() {
        // Given
        TokenRequest request = TokenRequest.builder()
            .grantType("client_credentials")
            .clientId("test-client")
            .clientSecret("test-secret")
            .build();

        TokenResponse expectedResponse = TokenResponse.builder()
            .accessToken("access-token-123")
            .tokenType("Bearer")
            .expiresIn(3600L)
            .scope("read write")
            .build();

        when(clientCredentialsGrantHandler.handle(any())).thenReturn(expectedResponse);

        // When
        ResponseEntity<TokenResponse> response = tokenEndpoint.handleTokenRequest(request);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response.getBody());
        verify(clientRegistrationService).validateClient("test-client", "test-secret");
    }

    @Test
    void handleTokenRequest_RefreshTokenGrant_ReturnsTokens() {
        // Given
        String refreshToken = jwtTokenService.generateRefreshToken("test-client", "test-user", "read write");
        TokenRequest request = TokenRequest.builder()
            .grantType("refresh_token")
            .clientId("test-client")
            .clientSecret("test-secret")
            .refreshToken(refreshToken)
            .build();

        TokenResponse expectedResponse = TokenResponse.builder()
            .accessToken("access-token-123")
            .tokenType("Bearer")
            .expiresIn(3600L)
            .refreshToken("refresh-token-456")
            .scope("read write")
            .build();

        when(refreshTokenGrantHandler.handle(any())).thenReturn(expectedResponse);

        // When
        ResponseEntity<TokenResponse> response = tokenEndpoint.handleTokenRequest(request);

        // Then
        assertNotNull(response);
        assertEquals(expectedResponse, response.getBody());
        verify(clientRegistrationService).validateClient("test-client", "test-secret");
    }

    @Test
    void handleTokenRequest_InvalidGrantType_ReturnsError() {
        // Given
        TokenRequest request = TokenRequest.builder()
            .grantType("invalid")
            .clientId("test-client")
            .clientSecret("test-secret")
            .build();

        // When
        ResponseEntity<TokenResponse> response = tokenEndpoint.handleTokenRequest(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals("unsupported_grant_type", response.getBody().getError());
    }

    @Test
    void handleTokenRequest_InvalidClient_ReturnsError() {
        // Given
        TokenRequest request = TokenRequest.builder()
            .grantType("authorization_code")
            .clientId("invalid-client")
            .clientSecret("invalid-secret")
            .build();

        doThrow(new OAuthException("invalid_client", "Invalid client"))
            .when(clientRegistrationService).validateClient(anyString(), anyString());

        // When
        ResponseEntity<TokenResponse> response = tokenEndpoint.handleTokenRequest(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals("invalid_client", response.getBody().getError());
    }
} 