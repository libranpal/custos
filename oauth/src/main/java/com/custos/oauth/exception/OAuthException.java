package com.custos.oauth.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Exception class for OAuth 2.1 errors.
 * This class follows the OAuth 2.1 specification for error responses.
 */
@Getter
public class OAuthException extends RuntimeException {
    @JsonProperty("error")
    private final String error;
    
    @JsonProperty("error_description")
    private final String errorDescription;
    
    public OAuthException(String error, String errorDescription) {
        super(errorDescription);
        this.error = error;
        this.errorDescription = errorDescription;
    }
    
    /**
     * Standard OAuth 2.1 error codes
     */
    public static class ErrorCodes {
        public static final String INVALID_REQUEST = "invalid_request";
        public static final String INVALID_CLIENT = "invalid_client";
        public static final String INVALID_GRANT = "invalid_grant";
        public static final String UNAUTHORIZED_CLIENT = "unauthorized_client";
        public static final String UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";
        public static final String INVALID_SCOPE = "invalid_scope";
        public static final String SERVER_ERROR = "server_error";
        public static final String TEMPORARILY_UNAVAILABLE = "temporarily_unavailable";
    }
} 