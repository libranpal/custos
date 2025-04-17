package com.custos.oauth.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClientRegistrationRequestTest {

    @Test
    void testBuilderWithAllFields() {
        ClientRegistrationRequest request = ClientRegistrationRequest.builder()
            .clientName("Test Client")
            .clientType("confidential")
            .redirectUris(new String[]{"https://example.com/callback"})
            .grantTypes(new String[]{"authorization_code", "client_credentials"})
            .responseTypes(new String[]{"code"})
            .scopes(new String[]{"openid", "profile"})
            .jwksUri("https://example.com/jwks.json")
            .jwks("{\"keys\":[{\"kty\":\"RSA\",\"n\":\"...\"}]}")
            .build();

        assertNotNull(request);
        assertEquals("Test Client", request.getClientName());
        assertEquals("confidential", request.getClientType());
        assertArrayEquals(new String[]{"https://example.com/callback"}, request.getRedirectUris());
        assertArrayEquals(new String[]{"authorization_code", "client_credentials"}, request.getGrantTypes());
        assertArrayEquals(new String[]{"code"}, request.getResponseTypes());
        assertArrayEquals(new String[]{"openid", "profile"}, request.getScopes());
        assertEquals("https://example.com/jwks.json", request.getJwksUri());
        assertEquals("{\"keys\":[{\"kty\":\"RSA\",\"n\":\"...\"}]}", request.getJwks());
    }

    @Test
    void testBuilderWithRequiredFieldsOnly() {
        ClientRegistrationRequest request = ClientRegistrationRequest.builder()
            .clientName("Test Client")
            .clientType("public")
            .grantTypes(new String[]{"authorization_code"})
            .build();

        assertNotNull(request);
        assertEquals("Test Client", request.getClientName());
        assertEquals("public", request.getClientType());
        assertArrayEquals(new String[]{"authorization_code"}, request.getGrantTypes());
        assertNull(request.getRedirectUris());
        assertNull(request.getResponseTypes());
        assertNull(request.getScopes());
        assertNull(request.getJwksUri());
        assertNull(request.getJwks());
    }

    @Test
    void testBuilderWithJwtAuthentication() {
        ClientRegistrationRequest request = ClientRegistrationRequest.builder()
            .clientName("JWT Client")
            .clientType("confidential")
            .grantTypes(new String[]{"client_credentials"})
            .jwksUri("https://jwt-client.com/jwks.json")
            .build();

        assertNotNull(request);
        assertEquals("JWT Client", request.getClientName());
        assertEquals("confidential", request.getClientType());
        assertArrayEquals(new String[]{"client_credentials"}, request.getGrantTypes());
        assertEquals("https://jwt-client.com/jwks.json", request.getJwksUri());
        assertNull(request.getJwks());
    }

    @Test
    void testBuilderWithInlineJwks() {
        ClientRegistrationRequest request = ClientRegistrationRequest.builder()
            .clientName("Inline JWKS Client")
            .clientType("confidential")
            .grantTypes(new String[]{"client_credentials"})
            .jwks("{\"keys\":[{\"kty\":\"RSA\",\"n\":\"...\"}]}")
            .build();

        assertNotNull(request);
        assertEquals("Inline JWKS Client", request.getClientName());
        assertEquals("confidential", request.getClientType());
        assertArrayEquals(new String[]{"client_credentials"}, request.getGrantTypes());
        assertNull(request.getJwksUri());
        assertEquals("{\"keys\":[{\"kty\":\"RSA\",\"n\":\"...\"}]}", request.getJwks());
    }
} 