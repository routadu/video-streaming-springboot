package com.akrdev.videostreamingtut.exception;

public class CustomAccessDeniedException extends RuntimeException
{
    public CustomAccessDeniedException() {
        super("Access denied to the requested resource");
    }
}
