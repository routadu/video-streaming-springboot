package com.akrdev.videostreamingtut.data.dto.video;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoUploadRequest {
    @NotBlank
    @Length(min = 5, max = 200)
    private String title;
    private String description;
    @Builder.Default
    private String contentType = "application/octet-stream";
}
