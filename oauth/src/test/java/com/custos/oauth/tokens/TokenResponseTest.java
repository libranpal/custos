/*
 * Copyright 2024 Custos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.custos.oauth.tokens;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenResponseTest {

    @Test
    void testConstructorWithAllFields() {
        TokenResponse response = new TokenResponse(
            "test-access-token",
            "Bearer",
            3600L,
            "test-refresh-token",
            "test-scope"
        );

        assertThat(response.getAccessToken()).isEqualTo("test-access-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(3600L);
        assertThat(response.getRefreshToken()).isEqualTo("test-refresh-token");
        assertThat(response.getScope()).isEqualTo("test-scope");
    }

    @Test
    void testConstructorWithRequiredFields() {
        TokenResponse response = new TokenResponse(
            "test-access-token",
            "Bearer",
            3600L,
            null,
            null
        );

        assertThat(response.getAccessToken()).isEqualTo("test-access-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(3600L);
        assertThat(response.getRefreshToken()).isNull();
        assertThat(response.getScope()).isNull();
    }

    @Test
    void testConstructorWithNullAccessToken() {
        assertThatThrownBy(() -> new TokenResponse(
            null,
            "Bearer",
            3600L,
            null,
            null
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Access token cannot be null");
    }

    @Test
    void testConstructorWithNullTokenType() {
        assertThatThrownBy(() -> new TokenResponse(
            "test-access-token",
            null,
            3600L,
            null,
            null
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Token type cannot be null");
    }

    @Test
    void testConstructorWithInvalidExpiresIn() {
        assertThatThrownBy(() -> new TokenResponse(
            "test-access-token",
            "Bearer",
            -1L,
            null,
            null
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Expires in must be positive");
    }
} 