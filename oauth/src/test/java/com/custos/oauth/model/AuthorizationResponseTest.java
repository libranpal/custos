package com.custos.oauth.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationResponseTest {

    @Test
    void testBuilderWithAllFields() {
        AuthorizationResponse response = AuthorizationResponse.builder()
                .code("test_code")
                .state("test_state")
                .error("invalid_request")
                .errorDescription("Invalid request parameters")
                .errorUri("https://example.com/errors/invalid_request")
                .build();

        assertNotNull(response);
        assertEquals("test_code", response.getCode());
        assertEquals("test_state", response.getState());
        assertEquals("invalid_request", response.getError());
        assertEquals("Invalid request parameters", response.getErrorDescription());
        assertEquals("https://example.com/errors/invalid_request", response.getErrorUri());
    }

    @Test
    void testBuilderWithRequiredFields() {
        AuthorizationResponse response = AuthorizationResponse.builder()
                .code("test_code")
                .build();

        assertNotNull(response);
        assertEquals("test_code", response.getCode());
        assertNull(response.getState());
        assertNull(response.getError());
        assertNull(response.getErrorDescription());
        assertNull(response.getErrorUri());
    }

    @Test
    void testBuilderWithNullCode() {
        assertThrows(NullPointerException.class, () -> {
            AuthorizationResponse.builder()
                    .code(null)
                    .build();
        });
    }
} 