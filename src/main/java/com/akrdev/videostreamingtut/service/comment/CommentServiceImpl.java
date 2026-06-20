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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
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

        Comment savedComment = commentRepository.save(comment);
        UUID parentCommentId = null;

        if(savedComment.getParentComment() != null) {
            parentCommentId = savedComment.getParentComment().getId();
        }

        return CommentResponseDto.builder()
                .id(savedComment.getId())
                .parentId(parentCommentId)
                .videoId(video.getId())
                .text(savedComment.getText())
                .authorUsername(savedComment.getAuthor().getUsername())
                .createdAt(savedComment.getCreatedAt())
                .updatedAt(savedComment.getUpdatedAt())
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
        if(pageable == null) {
            pageable = PageRequest.of(
                    0,
                    10,
                    Sort.by("updatedAt").descending());
        } else if (pageable.getSort().isEmpty()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("updatedAt").descending()
            );
        }
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
        if(!comment.getAuthor().getUsername().equals(requestingUsername)) {
            throw new CustomAccessDeniedException("You do not have permission to delete this comment");
        }
        Comment parentComment = comment.getParentComment();
        if(parentComment != null) {
            parentComment.decrementReplyCount();
        }
        commentRepository.delete(comment);
    }
}
