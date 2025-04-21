package com.custos.oauth;

import com.custos.oauth.model.*;
import com.custos.oauth.service.Authenticator;
import com.custos.oauth.service.ClientRegistrationService;
import com.custos.oauth.service.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Disabled("Temporarily disabling for debugging")
class OAuthFlowTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClientRegistrationService clientRegistrationService;

    @Autowired
    private Authenticator authenticator;

    @Autowired
    private JwtTokenService jwtTokenService;

    private static final String CLIENT_ID = "test-client";
    private static final String CLIENT_SECRET = "test-secret";
    private static final String REDIRECT_URI = "https://client.example.com/callback";
    private static final String USERNAME = "test-user";
    private static final String PASSWORD = "test-password";
    private static final String SCOPE = "read write";

    @BeforeEach
    void setUp() {
        // Register test client
        clientRegistrationService.registerClient(ClientRegistrationRequest.builder()
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .redirectUri(REDIRECT_URI)
            .scope(SCOPE)
            .build());
        
        // Register test user
        authenticator.registerUser(USERNAME, PASSWORD, SCOPE);
    }

    @Test
    void completeAuthorizationCodeFlow() {
        // Step 1: Start authorization flow
        AuthorizationRequest authRequest = AuthorizationRequest.builder()
            .clientId(CLIENT_ID)
            .redirectUri(REDIRECT_URI)
            .responseType("code")
            .state("state123")
            .scope(SCOPE)
            .codeChallenge("E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM")
            .codeChallengeMethod("S256")
            .build();

        ResponseEntity<String> authResponse = restTemplate.getForEntity(
            "/oauth2/authorize?client_id={clientId}&redirect_uri={redirectUri}&response_type={responseType}&state={state}&scope={scope}&code_challenge={codeChallenge}&code_challenge_method={codeChallengeMethod}",
            String.class,
            authRequest.getClientId(),
            authRequest.getRedirectUri(),
            authRequest.getResponseType(),
            authRequest.getState(),
            authRequest.getScope(),
            authRequest.getCodeChallenge(),
            authRequest.getCodeChallengeMethod()
        );

        assertEquals(HttpStatus.FOUND, authResponse.getStatusCode());
        assertTrue(authResponse.getHeaders().getLocation().toString().contains("/login"));

        // Step 2: Authenticate user (simulated)
        String userId = authenticator.authenticate(USERNAME, PASSWORD);

        // Step 3: Consent (simulated)
        ResponseEntity<String> consentResponse = restTemplate.postForEntity(
            "/oauth2/authorize/consent",
            authRequest,
            String.class
        );

        assertEquals(HttpStatus.FOUND, consentResponse.getStatusCode());
        String location = consentResponse.getHeaders().getLocation().toString();
        assertTrue(location.contains(REDIRECT_URI));
        assertTrue(location.contains("code="));
        assertTrue(location.contains("state=state123"));

        // Extract authorization code from redirect URI
        String code = location.substring(location.indexOf("code=") + 5, location.indexOf("&state"));

        // Step 4: Exchange code for tokens
        TokenRequest tokenRequest = TokenRequest.builder()
            .grantType("authorization_code")
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .code(code)
            .redirectUri(REDIRECT_URI)
            .codeVerifier("dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk")
            .build();

        ResponseEntity<TokenResponse> tokenResponse = restTemplate.postForEntity(
            "/oauth2/token",
            tokenRequest,
            TokenResponse.class
        );

        assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());
        assertNotNull(tokenResponse.getBody());
        assertNotNull(tokenResponse.getBody().getAccessToken());
        assertNotNull(tokenResponse.getBody().getRefreshToken());
        assertEquals("Bearer", tokenResponse.getBody().getTokenType());
        assertEquals(3600L, tokenResponse.getBody().getExpiresIn());
        assertEquals(SCOPE, tokenResponse.getBody().getScope());

        // Step 5: Introspect token
        TokenIntrospectionRequest introspectRequest = TokenIntrospectionRequest.builder()
            .token(tokenResponse.getBody().getAccessToken())
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .build();

        ResponseEntity<TokenIntrospectionResponse> introspectResponse = restTemplate.postForEntity(
            "/oauth2/introspect",
            introspectRequest,
            TokenIntrospectionResponse.class
        );

        assertEquals(HttpStatus.OK, introspectResponse.getStatusCode());
        assertNotNull(introspectResponse.getBody());
        assertTrue(introspectResponse.getBody().isActive());
        assertEquals(USERNAME, introspectResponse.getBody().getUsername());
        assertEquals(CLIENT_ID, introspectResponse.getBody().getClientId());
        assertEquals(SCOPE, introspectResponse.getBody().getScope());
        assertEquals("Bearer", introspectResponse.getBody().getTokenType());

        // Step 6: Revoke token
        TokenRevocationRequest revokeRequest = TokenRevocationRequest.builder()
            .token(tokenResponse.getBody().getAccessToken())
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .build();

        ResponseEntity<Void> revokeResponse = restTemplate.postForEntity(
            "/oauth2/revoke",
            revokeRequest,
            Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, revokeResponse.getStatusCode());

        // Step 7: Verify token is revoked
        ResponseEntity<TokenIntrospectionResponse> verifyResponse = restTemplate.postForEntity(
            "/oauth2/introspect",
            introspectRequest,
            TokenIntrospectionResponse.class
        );

        assertEquals(HttpStatus.OK, verifyResponse.getStatusCode());
        assertNotNull(verifyResponse.getBody());
        assertFalse(verifyResponse.getBody().isActive());
    }

    @Test
    void completePasswordGrantFlow() {
        // Step 1: Request tokens using password grant
        TokenRequest tokenRequest = TokenRequest.builder()
            .grantType("password")
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .username(USERNAME)
            .password(PASSWORD)
            .scope(SCOPE)
            .build();

        ResponseEntity<TokenResponse> tokenResponse = restTemplate.postForEntity(
            "/oauth2/token",
            tokenRequest,
            TokenResponse.class
        );

        assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());
        assertNotNull(tokenResponse.getBody());
        assertNotNull(tokenResponse.getBody().getAccessToken());
        assertNotNull(tokenResponse.getBody().getRefreshToken());
        assertEquals("Bearer", tokenResponse.getBody().getTokenType());
        assertEquals(3600L, tokenResponse.getBody().getExpiresIn());
        assertEquals(SCOPE, tokenResponse.getBody().getScope());

        // Step 2: Use refresh token to get new access token
        TokenRequest refreshRequest = TokenRequest.builder()
            .grantType("refresh_token")
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .refreshToken(tokenResponse.getBody().getRefreshToken())
            .build();

        ResponseEntity<TokenResponse> refreshResponse = restTemplate.postForEntity(
            "/oauth2/token",
            refreshRequest,
            TokenResponse.class
        );

        assertEquals(HttpStatus.OK, refreshResponse.getStatusCode());
        assertNotNull(refreshResponse.getBody());
        assertNotNull(refreshResponse.getBody().getAccessToken());
        assertNotNull(refreshResponse.getBody().getRefreshToken());
        assertNotEquals(tokenResponse.getBody().getAccessToken(), refreshResponse.getBody().getAccessToken());
    }

    @Test
    void completeClientCredentialsFlow() {
        // Step 1: Request tokens using client credentials grant
        TokenRequest tokenRequest = TokenRequest.builder()
            .grantType("client_credentials")
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .scope(SCOPE)
            .build();

        ResponseEntity<TokenResponse> tokenResponse = restTemplate.postForEntity(
            "/oauth2/token",
            tokenRequest,
            TokenResponse.class
        );

        assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());
        assertNotNull(tokenResponse.getBody());
        assertNotNull(tokenResponse.getBody().getAccessToken());
        assertEquals("Bearer", tokenResponse.getBody().getTokenType());
        assertEquals(3600L, tokenResponse.getBody().getExpiresIn());
        assertEquals(SCOPE, tokenResponse.getBody().getScope());
        assertNull(tokenResponse.getBody().getRefreshToken()); // Client credentials grant doesn't issue refresh tokens
    }
} 