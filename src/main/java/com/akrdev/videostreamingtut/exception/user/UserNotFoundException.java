package com.akrdev.videostreamingtut.exception.user;

public class UserNotFoundException extends UserException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException id(String username) {
        return new UserNotFoundException("User not found with username: " + username);
    }

    public static UserNotFoundException email(String email) {
        return new UserNotFoundException("User not found with email: " + email);
    }

    public static UserNotFoundException usernameOrEmail(String username, String email) {
        return new UserNotFoundException("User not found with username: " + username + " email: " + email);
    }
}
