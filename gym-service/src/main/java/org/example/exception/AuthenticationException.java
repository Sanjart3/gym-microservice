package org.example.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String username) {
        super("User with username " + username + " not authenticated");
    }
}
