package com.akrdev.videostreamingtut.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException id(Long id) {
        return new UserNotFoundException("User not found with id: " + id);
    }

    public static UserNotFoundException email(String email) {
        return new UserNotFoundException("User not found with email: " + email);
    }
}
