package com.custos.oauth.service;

import com.custos.oauth.exception.OAuthException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

/**
 * Service for handling JWT token operations according to RFC 9068.
 */
@Slf4j
@Service
public class JwtTokenService {

    private final RSAKey rsaKey;
    private final RSASSASigner signer;
    private final RSASSAVerifier verifier;

    public JwtTokenService() throws JOSEException {
        // Generate RSA key pair for signing and verifying tokens
        this.rsaKey = new RSAKeyGenerator(2048).generate();
        this.signer = new RSASSASigner(rsaKey);
        this.verifier = new RSASSAVerifier(rsaKey.toPublicJWK());
    }

    /**
     * Generates a JWT access token.
     *
     * @param clientId The client identifier
     * @param subject The subject (user ID)
     * @param scope The scope of the token
     * @param expiresInSeconds Token expiration time in seconds
     * @return The signed JWT access token
     * @throws OAuthException if token generation fails
     */
    public String generateAccessToken(String clientId, String subject, String scope, long expiresInSeconds) throws OAuthException {
        try {
            // Create JWT claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("https://auth.custos.com") // Your authorization server URL
                .subject(subject)
                .audience(clientId)
                .expirationTime(Date.from(Instant.now().plusSeconds(expiresInSeconds)))
                .notBeforeTime(Date.from(Instant.now()))
                .issueTime(Date.from(Instant.now()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", scope)
                .claim("client_id", clientId)
                .claim("token_type", "Bearer")
                .build();

            // Create signed JWT
            SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .keyID(rsaKey.getKeyID())
                    .build(),
                claimsSet
            );

            // Sign the JWT
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("Failed to generate access token", e);
            throw new OAuthException("server_error", "Failed to generate access token");
        }
    }

    /**
     * Generates a JWT refresh token.
     *
     * @param clientId The client identifier
     * @param subject The subject (user ID)
     * @param scope The scope of the token
     * @return The signed JWT refresh token
     * @throws OAuthException if token generation fails
     */
    public String generateRefreshToken(String clientId, String subject, String scope) throws OAuthException {
        try {
            // Create JWT claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("https://auth.custos.com") // Your authorization server URL
                .subject(subject)
                .audience(clientId)
                .expirationTime(Date.from(Instant.now().plusSeconds(7 * 24 * 60 * 60))) // 7 days
                .notBeforeTime(Date.from(Instant.now()))
                .issueTime(Date.from(Instant.now()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", scope)
                .claim("client_id", clientId)
                .claim("token_type", "refresh_token")
                .build();

            // Create signed JWT
            SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .keyID(rsaKey.getKeyID())
                    .build(),
                claimsSet
            );

            // Sign the JWT
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("Failed to generate refresh token", e);
            throw new OAuthException("server_error", "Failed to generate refresh token");
        }
    }

    /**
     * Validates a JWT token.
     *
     * @param token The JWT token to validate
     * @return The JWT claims set if valid
     * @throws OAuthException if token is invalid
     */
    public JWTClaimsSet validateToken(String token) throws OAuthException {
        try {
            // Parse the token
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Verify the signature
            if (!signedJWT.verify(verifier)) {
                throw new OAuthException("invalid_token", "Invalid token signature");
            }

            // Get the claims
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // Check expiration
            if (claimsSet.getExpirationTime().before(Date.from(Instant.now()))) {
                throw new OAuthException("invalid_token", "Token has expired");
            }

            // Check not before
            if (claimsSet.getNotBeforeTime().after(Date.from(Instant.now()))) {
                throw new OAuthException("invalid_token", "Token not yet valid");
            }

            return claimsSet;
        } catch (ParseException | JOSEException e) {
            log.error("Failed to validate token", e);
            throw new OAuthException("invalid_token", "Invalid token");
        }
    }

    /**
     * Gets the public key for token verification.
     *
     * @return The public key in JWK format
     */
    public String getPublicKey() {
        return rsaKey.toPublicJWK().toJSONString();
    }

    /**
     * Generates an authorization code for the authorization code flow.
     *
     * @param clientId The client identifier
     * @param userId The user identifier
     * @param redirectUri The redirect URI
     * @param scope The scope of the authorization
     * @param codeChallenge The PKCE code challenge
     * @param codeChallengeMethod The PKCE code challenge method
     * @return The authorization code
     * @throws OAuthException if code generation fails
     */
    public String generateAuthorizationCode(
        String clientId,
        String userId,
        String redirectUri,
        String scope,
        String codeChallenge,
        String codeChallengeMethod
    ) throws OAuthException {
        try {
            // Create JWT claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("https://auth.custos.com") // Your authorization server URL
                .subject(userId)
                .audience(clientId)
                .expirationTime(Date.from(Instant.now().plusSeconds(300))) // 5 minutes
                .notBeforeTime(Date.from(Instant.now()))
                .issueTime(Date.from(Instant.now()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", scope)
                .claim("client_id", clientId)
                .claim("redirect_uri", redirectUri)
                .claim("code_challenge", codeChallenge)
                .claim("code_challenge_method", codeChallengeMethod)
                .build();

            // Create signed JWT
            SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .keyID(rsaKey.getKeyID())
                    .build(),
                claimsSet
            );

            // Sign the JWT
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("Failed to generate authorization code", e);
            throw new OAuthException("server_error", "Failed to generate authorization code");
        }
    }

    /**
     * Validates an authorization code and returns its claims.
     *
     * @param code The authorization code to validate
     * @param codeVerifier The PKCE code verifier
     * @return The JWT claims set if valid
     * @throws OAuthException if code is invalid
     */
    public JWTClaimsSet validateAuthorizationCode(String code, String codeVerifier) throws OAuthException {
        try {
            // Parse the code
            SignedJWT signedJWT = SignedJWT.parse(code);

            // Verify the signature
            if (!signedJWT.verify(verifier)) {
                throw new OAuthException("invalid_grant", "Invalid authorization code");
            }

            // Get the claims
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // Check expiration
            if (claimsSet.getExpirationTime().before(Date.from(Instant.now()))) {
                throw new OAuthException("invalid_grant", "Authorization code has expired");
            }

            // Check not before
            if (claimsSet.getNotBeforeTime().after(Date.from(Instant.now()))) {
                throw new OAuthException("invalid_grant", "Authorization code not yet valid");
            }

            // Validate PKCE if code challenge was provided
            String codeChallenge = claimsSet.getStringClaim("code_challenge");
            String codeChallengeMethod = claimsSet.getStringClaim("code_challenge_method");
            
            if (codeChallenge != null && codeVerifier == null) {
                throw new OAuthException("invalid_grant", "Code verifier is required");
            }
            
            if (codeChallenge != null && codeVerifier != null) {
                String computedChallenge;
                if ("S256".equals(codeChallengeMethod)) {
                    computedChallenge = Base64.getUrlEncoder().withoutPadding()
                        .encodeToString(MessageDigest.getInstance("SHA-256")
                            .digest(codeVerifier.getBytes(StandardCharsets.US_ASCII)));
                } else if ("plain".equals(codeChallengeMethod)) {
                    computedChallenge = codeVerifier;
                } else {
                    throw new OAuthException("invalid_grant", "Unsupported code challenge method");
                }
                
                if (!codeChallenge.equals(computedChallenge)) {
                    throw new OAuthException("invalid_grant", "Invalid code verifier");
                }
            }

            return claimsSet;
        } catch (ParseException | JOSEException | NoSuchAlgorithmException e) {
            log.error("Failed to validate authorization code", e);
            throw new OAuthException("invalid_grant", "Invalid authorization code");
        }
    }
} 