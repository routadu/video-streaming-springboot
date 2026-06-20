package com.akrdev.videostreamingtut.exception.comment;

public class DoubleNestedCommentException extends CommentException {
    public DoubleNestedCommentException(String message) {
        super(message);
    }

    public DoubleNestedCommentException() {
        super("Replying to reply is not allowed");
    }
}
