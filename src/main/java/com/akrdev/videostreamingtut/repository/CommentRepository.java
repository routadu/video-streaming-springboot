package com.akrdev.videostreamingtut.repository;

import com.akrdev.videostreamingtut.entity.comments.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    Page<Comment> findByVideoIdAndParentCommentIsNull(UUID videoId, Pageable pageable);

    Page<Comment> findByParentCommentId(UUID parentCommentId, Pageable pageable);

    long countByVideoId(UUID videoId);

    long countByParentCommentId(UUID parentId);
}
