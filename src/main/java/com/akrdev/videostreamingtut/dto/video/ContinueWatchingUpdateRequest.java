package com.akrdev.videostreamingtut.dto.video;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
public class ContinueWatchingUpdateRequest {
    private String username;
    @NotNull
    private UUID videoId;
    @PositiveOrZero
    private long lastPlaybackSecond;
}
