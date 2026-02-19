package com.akrdev.videostreamingtut.service.videofile;

import com.akrdev.videostreamingtut.entity.video.videofile.VideoFile;
import com.akrdev.videostreamingtut.entity.video.videofile.processingstatus.ProcessingStatus;
import com.akrdev.videostreamingtut.exception.CustomAccessDeniedException;
import com.akrdev.videostreamingtut.exception.VideoFileNotFoundException;
import com.akrdev.videostreamingtut.repository.VideoFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoFileServiceImpl implements VideoFileService {

    private final VideoFileRepository repository;

    @Value("${video.default-local-hls-directory}")
    private String defaultLocalHlsDirectory;

    public VideoFile saveFile(VideoFile videoFile) {
        return repository.save(videoFile);
    }

    @Override
    public Optional<VideoFile> findByVideoIdAndResolution(UUID videoId, int resolution) {
        return repository.findByVideoIdAndResolution(videoId, resolution);
    }

    @Override
    public VideoFile findByVideoIdAndResolutionOrThrow(UUID videoId, int resolution) {
        return findByVideoIdAndResolution(videoId, resolution)
                .orElseThrow(() -> new VideoFileNotFoundException(videoId, resolution));
    }

    @Override
    public VideoFile updateVideoFileProcessingStatus(UUID videoId, int resolution, ProcessingStatus newStatus) throws VideoFileNotFoundException {
        VideoFile videoFile = findByVideoIdAndResolutionOrThrow(videoId, resolution);
        if(!videoFile.getProcessingStatus().equals(ProcessingStatus.IN_PROGRESS)) {
            log.warn("Invalid processingStatus update for videoId: {}/{} from: {} to: {}",
                    videoId.toString(),
                    resolution,
                    videoFile.getProcessingStatus(),
                    newStatus
            );
            return videoFile;
        }
        videoFile.setProcessingStatus(newStatus);
        log.info("Updated processingStatus for videoId: {} to: {}", videoFile.getVideoId(), newStatus);
        return saveFile(videoFile);
    }

    @Override
    public Resource getSegmentFile(UUID videoId, String resolution, String segmentFileName) {
        Path baseVideoDir = Paths.get(defaultLocalHlsDirectory, videoId.toString()).toAbsolutePath().normalize();
        Path requestedFile = baseVideoDir.resolve(Paths.get(resolution, segmentFileName)).normalize();

        if (!requestedFile.startsWith(baseVideoDir)) {
            throw new CustomAccessDeniedException();
        }

        return new FileSystemResource(requestedFile);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void performCleanup() {
        log.info("Starting cleanup process");

        List<VideoFile> failedVideoFiles = repository.findAllByProcessingStatus(ProcessingStatus.FAILED);
        log.info("Failed video file(s) count: {}", failedVideoFiles.size());

        List<VideoFile> deletedVideoFiles = new ArrayList<>();
        for (VideoFile videoFile : failedVideoFiles) {
            Path path = Paths.get(videoFile.getPath());
            if(path.toFile().exists()) {
                try {
                    Files.delete(path);
                    log.info("Deleted video file: {}", videoFile.getPath());
                    deletedVideoFiles.add(videoFile);
                } catch (IOException e) {
                    log.error("Could not delete video file: {}", videoFile.getPath(), e);
                }
            }
        }
        log.info("Deleted video file(s) count: {}", deletedVideoFiles.size());
        repository.deleteAll(deletedVideoFiles);

        log.info("Cleanup process complete");
    }
}
