package com.custos.oauth.config;

import com.custos.oauth.grant.*;
import com.custos.oauth.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestConfig {
    
    @Bean
    @Primary
    public JwtTokenService jwtTokenService() throws Exception {
        return new JwtTokenService();
    }

    @Bean
    @Primary
    public Authenticator authenticator() {
        return new Authenticator() {
            @Override
            public String authenticate(String username, String password) {
                return username;
            }

            @Override
            public void validateScope(String userId, String scope) {
                // No-op for testing
            }
        };
    }

    @Bean
    @Primary
    public PasswordGrantHandler passwordGrantHandler(Authenticator authenticator, JwtTokenService jwtTokenService) {
        return new PasswordGrantHandler(authenticator, jwtTokenService);
    }

    @Bean
    @Primary
    public ClientCredentialsGrantHandler clientCredentialsGrantHandler(ClientRegistrationService clientRegistrationService, JwtTokenService jwtTokenService) {
        return new ClientCredentialsGrantHandler(clientRegistrationService, jwtTokenService);
    }

    @Bean
    @Primary
    public RefreshTokenGrantHandler refreshTokenGrantHandler(JwtTokenService jwtTokenService) {
        return new RefreshTokenGrantHandler(jwtTokenService);
    }

    @Bean
    @Primary
    public AuthorizationCodeGrantHandler authorizationCodeGrantHandler(JwtTokenService jwtTokenService) {
        return new AuthorizationCodeGrantHandler(jwtTokenService);
    }
} 