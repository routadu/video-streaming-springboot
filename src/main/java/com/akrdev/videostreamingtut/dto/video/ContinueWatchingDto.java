package com.akrdev.videostreamingtut.dto.video;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ContinueWatchingDto {
    private UUID videoId;
    private String title;
    private String thumbnailUrl;
    private long lastPlaybackSecond;
}
