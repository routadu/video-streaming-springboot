package com.akrdev.videostreamingtut.mapper;

import com.akrdev.videostreamingtut.dto.comment.CommentResponseDto;
import com.akrdev.videostreamingtut.entity.comments.Comment;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CommentResponseDtoMapper implements Function<Comment, CommentResponseDto> {
    @Override
    public CommentResponseDto apply(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .parentId(comment.getParentComment().getId())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .authorUsername(comment.getAuthor().getUsername())
                .replyCount(comment.getReplyCount())
                .build();
    }
}
