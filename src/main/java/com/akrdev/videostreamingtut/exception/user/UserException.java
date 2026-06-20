package com.akrdev.videostreamingtut.exception.user;

public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }

    public static UserException invalidUser(){
        return new UserException("Invalid user");
    }
}
