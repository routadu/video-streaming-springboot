package com.akrdev.videostreamingtut.service.comment;

import com.akrdev.videostreamingtut.dto.comment.CommentRequestDto;
import com.akrdev.videostreamingtut.dto.comment.CommentResponseDto;
import com.akrdev.videostreamingtut.entity.comments.Comment;
import com.akrdev.videostreamingtut.exception.CommentNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CommentService {
    CommentResponseDto createComment(CommentRequestDto requestDto);
    Optional<Comment> findCommentById(UUID commentId);
    Comment findCommentByIdOrThrow(UUID commentId) throws CommentNotFoundException;
    Comment findParentCommentByIdOrThrow(UUID parentCommentId) throws CommentNotFoundException;
    Optional<CommentResponseDto> findCommentDtoById(UUID commentId);
    CommentResponseDto findCommentDtoByIdOrThrow(UUID commentId) throws CommentNotFoundException;
    CommentResponseDto findParentCommentDtoByIdOrThrow(UUID parentCommentId) throws CommentNotFoundException;
    Page<CommentResponseDto> getTopLevelComments(UUID videoId, Pageable pageable);
    Page<CommentResponseDto> getReplies(UUID parentId, Pageable pageable);
    void deleteComment(UUID commentId, String requestingUsername);
}
