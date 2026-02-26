package com.akrdev.videostreamingtut.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CommentRequestDto {

    @NotBlank(message = "Comment text cannot be empty")
    @Size(max = 1000, message = "Comment exceeds the maximum length of 1000 characters")
    private String text;

    private String authorUsername;

    @NotNull
    private UUID videoId;

    private UUID parentId;
}
