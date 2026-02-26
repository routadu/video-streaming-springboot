package com.akrdev.videostreamingtut.exception;

public class DoubleNestedCommentException extends RuntimeException {
    public DoubleNestedCommentException(String message) {
        super(message);
    }

    public DoubleNestedCommentException() {
        super("Replying to reply is not allowed");
    }
}
