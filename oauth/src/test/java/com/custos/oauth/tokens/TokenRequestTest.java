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

class TokenRequestTest {

    @Test
    void testBuilderWithAllFields() {
        TokenRequest request = TokenRequest.builder()
            .grantType("authorization_code")
            .clientId("test-client")
            .clientSecret("test-secret")
            .username("test-user")
            .password("test-password")
            .refreshToken("test-refresh-token")
            .assertion("test-assertion")
            .scope("test-scope")
            .build();

        assertThat(request.getGrantType()).isEqualTo("authorization_code");
        assertThat(request.getClientId()).isEqualTo("test-client");
        assertThat(request.getClientSecret()).isEqualTo("test-secret");
        assertThat(request.getUsername()).isEqualTo("test-user");
        assertThat(request.getPassword()).isEqualTo("test-password");
        assertThat(request.getRefreshToken()).isEqualTo("test-refresh-token");
        assertThat(request.getAssertion()).isEqualTo("test-assertion");
        assertThat(request.getScope()).isEqualTo("test-scope");
    }

    @Test
    void testBuilderWithRequiredFields() {
        TokenRequest request = TokenRequest.builder()
            .grantType("authorization_code")
            .clientId("test-client")
            .build();

        assertThat(request.getGrantType()).isEqualTo("authorization_code");
        assertThat(request.getClientId()).isEqualTo("test-client");
        assertThat(request.getClientSecret()).isNull();
        assertThat(request.getUsername()).isNull();
        assertThat(request.getPassword()).isNull();
        assertThat(request.getRefreshToken()).isNull();
        assertThat(request.getAssertion()).isNull();
        assertThat(request.getScope()).isNull();
    }

    @Test
    void testBuilderWithNullGrantType() {
        assertThatThrownBy(() -> TokenRequest.builder()
            .clientId("test-client")
            .build()
        ).isInstanceOf(IllegalStateException.class)
         .hasMessage("Grant type is required");
    }

    @Test
    void testBuilderWithNullClientId() {
        assertThatThrownBy(() -> TokenRequest.builder()
            .grantType("authorization_code")
            .build()
        ).isInstanceOf(IllegalStateException.class)
         .hasMessage("Client ID is required");
    }
} 