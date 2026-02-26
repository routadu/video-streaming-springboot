package com.akrdev.videostreamingtut.service.comment;

import com.akrdev.videostreamingtut.dto.comment.CommentRequestDto;
import com.akrdev.videostreamingtut.dto.comment.CommentResponseDto;
import com.akrdev.videostreamingtut.entity.comments.Comment;
import com.akrdev.videostreamingtut.entity.user.User;
import com.akrdev.videostreamingtut.entity.video.Video;
import com.akrdev.videostreamingtut.exception.CommentNotFoundException;
import com.akrdev.videostreamingtut.exception.CustomAccessDeniedException;
import com.akrdev.videostreamingtut.exception.DoubleNestedCommentException;
import com.akrdev.videostreamingtut.mapper.CommentResponseDtoMapper;
import com.akrdev.videostreamingtut.repository.CommentRepository;
import com.akrdev.videostreamingtut.service.user.UserService;
import com.akrdev.videostreamingtut.service.video.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentResponseDtoMapper  commentResponseDtoMapper;

    private UserService userService;
    private VideoService videoService;

    @Autowired
    @Lazy
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    @Lazy
    public void setVideoService(VideoService videoService) {
        this.videoService = videoService;
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(CommentRequestDto requestDto) {
        User author = userService.findByUsernameOrThrow(requestDto.getAuthorUsername());
        Video video = videoService.findByIdOrThrow(requestDto.getVideoId());

        Comment comment = Comment.builder()
                .text(requestDto.getText())
                .author(author)
                .video(video)
                .build();

        if (requestDto.getParentId() != null) {
            Comment parent = findParentCommentByIdOrThrow(requestDto.getParentId());

            if (parent.getParentComment() != null) {
                throw new DoubleNestedCommentException();
            }

            comment.setParentComment(parent);
            parent.incrementReplyCount();
        }

        commentRepository.save(comment);

        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorUsername(comment.getAuthor().getUsername())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    @Override
    public Optional<Comment> findCommentById(UUID commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    public Comment findCommentByIdOrThrow(UUID commentId) throws CommentNotFoundException {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> CommentNotFoundException.comment(commentId));
    }

    @Override
    public Comment findParentCommentByIdOrThrow(UUID parentCommentId) throws CommentNotFoundException {
        return findCommentById(parentCommentId)
                .orElseThrow(() -> CommentNotFoundException.parent(parentCommentId));
    }

    @Override
    public Optional<CommentResponseDto> findCommentDtoById(UUID commentId) {
        return findCommentById(commentId)
                .map(commentResponseDtoMapper);
    }

    @Override
    public CommentResponseDto findCommentDtoByIdOrThrow(UUID commentId) throws CommentNotFoundException {
        return findCommentDtoById(commentId)
                .orElseThrow(() -> CommentNotFoundException.comment(commentId));
    }

    @Override
    public CommentResponseDto findParentCommentDtoByIdOrThrow(UUID parentCommentId) throws CommentNotFoundException {
        return findCommentDtoById(parentCommentId)
                .orElseThrow(() -> CommentNotFoundException.parent(parentCommentId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getTopLevelComments(UUID videoId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByVideoIdAndParentCommentIsNull(videoId, pageable);
        return comments.map(commentResponseDtoMapper);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getReplies(UUID parentId, Pageable pageable) {
        Page<Comment> replies = commentRepository.findByParentCommentId(parentId, pageable);
        return replies.map(commentResponseDtoMapper);
    }

    @Override
    public void deleteComment(UUID commentId, String requestingUsername) {
        Comment comment = findCommentByIdOrThrow(commentId);

        if(comment.getAuthor().getUsername().equals(requestingUsername)) {
            throw new CustomAccessDeniedException("You do not have permission to delete this comment");
        }

        Comment parentComment = comment.getParentComment();

        if(parentComment != null) {
            parentComment.decrementReplyCount();
        }

        commentRepository.delete(comment);
    }
}
