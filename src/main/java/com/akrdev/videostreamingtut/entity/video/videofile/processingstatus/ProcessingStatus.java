package com.akrdev.videostreamingtut.entity.video.videofile.processingstatus;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProcessingStatus {

    IN_PROGRESS ("in_progress"),
    COMPLETED ("completed"),
    FAILED ("failed");

    private final String value;

    ProcessingStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
