package com.akrdev.videostreamingtut.exception;

import java.util.UUID;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message) {
        super(message);
    }

    public static CommentNotFoundException comment(UUID commentId) {
        return new CommentNotFoundException("Comment not found with id: " + commentId);
    }

    public static CommentNotFoundException parent(UUID parentId) {
        return new CommentNotFoundException("Parent comment not found: " + parentId);
    }
}
