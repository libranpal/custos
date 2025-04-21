package com.custos.oauth;

import com.custos.oauth.exception.OAuthException;
import com.custos.oauth.model.TokenRevocationRequest;
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
 * OAuth 2.0 Token Revocation Endpoint implementation.
 * Handles token revocation requests according to RFC 7009.
 */
@Slf4j
@RestController
@RequestMapping("/oauth2/revoke")
@RequiredArgsConstructor
public class TokenRevocationEndpoint {

    private final JwtTokenService jwtTokenService;
    private final ClientRegistrationService clientRegistrationService;

    /**
     * Handles token revocation requests.
     *
     * @param request The token revocation request
     * @return ResponseEntity with no content
     */
    @PostMapping
    public ResponseEntity<Void> revokeToken(@RequestBody TokenRevocationRequest request) {
        log.info("Received token revocation request");
        
        try {
            // Validate client credentials
            clientRegistrationService.validateClient(request.getClientId(), request.getClientSecret());
            
            // Validate the token
            var claimsSet = jwtTokenService.validateToken(request.getToken());
            
            // Verify the token belongs to the client
            String tokenClientId = claimsSet.getStringClaim("client_id");
            if (!request.getClientId().equals(tokenClientId)) {
                throw new OAuthException("invalid_client", "Token does not belong to the client");
            }
            
            // TODO: Implement token revocation logic (e.g., add to a blacklist)
            
            return ResponseEntity.noContent().build();
            
        } catch (OAuthException | ParseException e) {
            log.error("Token revocation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
} 