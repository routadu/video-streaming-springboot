package com.akrdev.videostreamingtut.dto.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CommentResponseDto {
    private UUID id;
    private UUID parentId;
    private String text;
    private UUID videoId;
    private String authorUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long replyCount;
}
