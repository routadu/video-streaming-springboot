package com.akrdev.videostreamingtut.repository;

import com.akrdev.videostreamingtut.entity.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {

    @Query("SELECT v FROM Video v WHERE LOWER(v.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(v.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Video> findAllByQuery(@Param("query") String query);

}
