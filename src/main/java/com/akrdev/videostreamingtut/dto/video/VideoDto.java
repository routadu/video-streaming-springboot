package com.akrdev.videostreamingtut.dto.video;

import com.akrdev.videostreamingtut.entity.video.resolution.VideoResolution;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoDto {
    private UUID videoId;
    private String title;
    private String description;
    private String thumbnailUrl;
    private LocalDateTime uploadedOn;
    private LocalDateTime lastUpdatedOn;
    private List<Integer> resolutions;
}
