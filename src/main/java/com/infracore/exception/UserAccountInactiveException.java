package com.infracore.exception;

/**
 * Exception thrown when user account is not active
 */
public class UserAccountInactiveException extends RuntimeException {
    public UserAccountInactiveException(String message) {
        super(message);
    }
    
    public UserAccountInactiveException(String message, Throwable cause) {
        super(message, cause);
    }
} 