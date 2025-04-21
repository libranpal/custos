package com.custos.oauth;

import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.grant.ClientCredentialsGrantHandler;
import com.custos.oauth.grant.GrantHandler;
import com.custos.oauth.grant.PasswordGrantHandler;
import com.custos.oauth.grant.RefreshTokenGrantHandler;
import com.custos.oauth.grant.AuthorizationCodeGrantHandler;
import com.custos.oauth.model.TokenRequest;
import com.custos.oauth.model.TokenResponse;
import com.custos.oauth.service.Authenticator;
import com.custos.oauth.service.ClientRegistrationService;
import com.custos.oauth.service.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * OAuth 2.1 Token Endpoint implementation.
 * Handles token requests and responses according to RFC 6749 and RFC 9068.
 */
@Slf4j
@RestController
@RequestMapping("/oauth2/token")
public class TokenEndpoint {

    private final ClientRegistrationService clientRegistrationService;
    private final JwtTokenService jwtTokenService;
    private final Authenticator authenticator;
    
    private final Map<String, GrantHandler> grantHandlers = new HashMap<>();
    
    /**
     * Initializes the grant handlers.
     */
    public TokenEndpoint(ClientRegistrationService clientRegistrationService,
                        JwtTokenService jwtTokenService,
                        Authenticator authenticator) {
        this.clientRegistrationService = clientRegistrationService;
        this.jwtTokenService = jwtTokenService;
        this.authenticator = authenticator;
        
        // Register grant handlers
        grantHandlers.put("password", new PasswordGrantHandler(authenticator, jwtTokenService));
        grantHandlers.put("client_credentials", new ClientCredentialsGrantHandler(clientRegistrationService, jwtTokenService));
        grantHandlers.put("refresh_token", new RefreshTokenGrantHandler(jwtTokenService));
        grantHandlers.put("authorization_code", new AuthorizationCodeGrantHandler(jwtTokenService));
    }

    /**
     * Handles token requests for various grant types.
     *
     * @param request The token request
     * @return ResponseEntity containing the token response
     */
    @PostMapping
    public ResponseEntity<TokenResponse> handleTokenRequest(@RequestBody TokenRequest request) {
        log.info("Received token request with grant type: {}", request.getGrantType());
        
        try {
            // Validate client credentials
            clientRegistrationService.validateClient(request.getClientId(), request.getClientSecret());
            
            // Get the appropriate grant handler
            GrantHandler handler = grantHandlers.get(request.getGrantType());
            if (handler == null) {
                throw new OAuthException("unsupported_grant_type", 
                    "Grant type not supported: " + request.getGrantType());
            }
            
            // Handle the token request
            TokenResponse response = handler.handle(request);
            return ResponseEntity.ok(response);
            
        } catch (OAuthException e) {
            log.error("Token request failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(TokenResponse.builder()
                    .error(e.getErrorCode())
                    .errorDescription(e.getMessage())
                    .build());
        }
    }
} 