package com.akrdev.videostreamingtut.data.dto.video;

import com.fasterxml.jackson.annotation.JsonInclude;
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
