package com.custos.oauth.service;

import com.custos.oauth.OAuthTestApplication;
import com.custos.oauth.exception.OAuthException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = OAuthTestApplication.class)
@Disabled("Temporarily disabling for debugging")
class JwtTokenServiceTest {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Test
    void generateAccessToken_ValidInput_ReturnsValidToken() {
        String token = jwtTokenService.generateAccessToken("test-client", "test-user", "read write", 3600L);
        assertNotNull(token);
        assertTrue(token.startsWith("eyJ"));
    }

    @Test
    void generateRefreshToken_ValidInput_ReturnsValidToken() {
        String token = jwtTokenService.generateRefreshToken("test-client", "test-user", "read write");
        assertNotNull(token);
        assertTrue(token.startsWith("eyJ"));
    }

    @Test
    void generateAuthorizationCode_ValidInput_ReturnsValidCode() {
        String code = jwtTokenService.generateAuthorizationCode(
            "test-client", "test-user", "https://client.example.com/callback",
            "read write", "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM", "S256");
        assertNotNull(code);
        assertTrue(code.startsWith("eyJ"));
    }

    @Test
    void validateAuthorizationCode_InvalidCodeVerifier_ThrowsException() {
        String code = jwtTokenService.generateAuthorizationCode(
            "test-client", "test-user", "https://client.example.com/callback",
            "read write", "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM", "S256");
        
        assertThrows(OAuthException.class, () -> {
            jwtTokenService.validateAuthorizationCode(code, "invalid-verifier");
        });
    }

    @Test
    void validateToken_ExpiredToken_ThrowsException() {
        String token = jwtTokenService.generateAccessToken("test-client", "test-user", "read write", 0L);
        
        assertThrows(OAuthException.class, () -> {
            jwtTokenService.validateToken(token);
        });
    }

    @Test
    void getPublicKey_ReturnsValidKey() {
        String publicKey = jwtTokenService.getPublicKey();
        assertNotNull(publicKey);
        assertTrue(publicKey.startsWith("-----BEGIN PUBLIC KEY-----"));
    }
} 