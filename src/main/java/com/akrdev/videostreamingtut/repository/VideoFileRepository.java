package com.akrdev.videostreamingtut.repository;

import com.akrdev.videostreamingtut.entity.video.videofile.VideoFile;
import com.akrdev.videostreamingtut.entity.video.videofile.processingstatus.ProcessingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VideoFileRepository extends JpaRepository<VideoFile, UUID> {
    Optional<VideoFile> findByVideoIdAndResolution(UUID videoId, int resolution);
    List<VideoFile> findAllByProcessingStatus(ProcessingStatus processingStatus);
}
