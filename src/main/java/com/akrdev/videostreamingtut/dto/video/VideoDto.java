package com.akrdev.videostreamingtut.dto.video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDto {
    private UUID videoId;
    private String title;
    private String description;
    private String thumbnailUrl;
    private LocalDateTime uploadedOn;
    private LocalDateTime lastUpdatedOn;
}
