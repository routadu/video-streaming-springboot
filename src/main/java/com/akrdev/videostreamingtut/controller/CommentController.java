package com.akrdev.videostreamingtut.controller;

import com.akrdev.videostreamingtut.dto.comment.CommentResponseDto;
import com.akrdev.videostreamingtut.entity.user.UserDetailsImpl;
import com.akrdev.videostreamingtut.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<Page<CommentResponseDto>> getReplies(
            @PathVariable UUID commentId,
            Pageable pageable
    ) {
        Page<CommentResponseDto> replies = commentService.getReplies(commentId, pageable);
        return ResponseEntity.ok(replies);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID commentId,
            @AuthenticationPrincipal  UserDetailsImpl userDetails
    ) {
        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

}
