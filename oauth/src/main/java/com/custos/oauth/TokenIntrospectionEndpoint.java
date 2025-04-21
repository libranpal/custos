package com.custos.oauth;

import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.model.TokenIntrospectionRequest;
import com.custos.oauth.model.TokenIntrospectionResponse;
import com.custos.oauth.service.ClientRegistrationService;
import com.custos.oauth.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

/**
 * OAuth 2.0 Token Introspection Endpoint implementation.
 * Handles token introspection requests according to RFC 7662.
 */
@Slf4j
@RestController
@RequestMapping("/oauth2/introspect")
@RequiredArgsConstructor
public class TokenIntrospectionEndpoint {

    private final JwtTokenService jwtTokenService;
    private final ClientRegistrationService clientRegistrationService;

    /**
     * Handles token introspection requests.
     *
     * @param request The token introspection request
     * @return ResponseEntity containing the token introspection response
     */
    @PostMapping
    public ResponseEntity<TokenIntrospectionResponse> introspectToken(@RequestBody TokenIntrospectionRequest request) {
        log.info("Received token introspection request");
        
        try {
            // Validate client credentials
            clientRegistrationService.validateClient(request.getClientId(), request.getClientSecret());
            
            // Validate the token
            var claimsSet = jwtTokenService.validateToken(request.getToken());
            
            // Build response
            TokenIntrospectionResponse response = TokenIntrospectionResponse.builder()
                .active(true)
                .scope(claimsSet.getStringClaim("scope"))
                .clientId(claimsSet.getStringClaim("client_id"))
                .username(claimsSet.getSubject())
                .tokenType(claimsSet.getStringClaim("token_type"))
                .exp(claimsSet.getExpirationTime().getTime() / 1000)
                .iat(claimsSet.getIssueTime().getTime() / 1000)
                .nbf(claimsSet.getNotBeforeTime().getTime() / 1000)
                .sub(claimsSet.getSubject())
                .aud(claimsSet.getAudience().get(0))
                .iss(claimsSet.getIssuer())
                .jti(claimsSet.getJWTID())
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (OAuthException | ParseException e) {
            log.error("Token introspection failed: {}", e.getMessage());
            return ResponseEntity.ok(TokenIntrospectionResponse.builder()
                .active(false)
                .build());
        }
    }
} 