package com.akrdev.videostreamingtut.service.process;

import com.akrdev.videostreamingtut.dto.video.UploadedVideoDto;
import com.akrdev.videostreamingtut.entity.video.Video;
import com.akrdev.videostreamingtut.entity.video.resolution.VideoResolution;
import com.akrdev.videostreamingtut.entity.video.videofile.VideoFile;
import com.akrdev.videostreamingtut.entity.video.videofile.processingstatus.ProcessingStatus;
import com.akrdev.videostreamingtut.service.video.VideoService;
import com.akrdev.videostreamingtut.service.videofile.VideoFileService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class FFmpegVideoProcessingServiceImpl implements VideoProcessingService {

    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoFileService videoFileService;

    @Value("${thumbnail.default-local-directory}")
    private String thumbnailLocalDirectory;

    @Value("${video.default-local-directory}")
    private String defaultLocalDirectory;

    @Value("${video.default-local-hls-directory}")
    private String defaultLocalHlsDirectory;

    @PostConstruct
    public void init() {
        try{
            Path videoHlsPathObj = Path.of(defaultLocalHlsDirectory);
            Path thumbnailPathObj = Path.of(thumbnailLocalDirectory);

            if(Files.notExists(videoHlsPathObj)){
                log.info("Creating hls directory: {}",  defaultLocalHlsDirectory);
                Files.createDirectories(videoHlsPathObj);
            }
            if(Files.notExists(thumbnailPathObj)){
                log.info("Creating thumbnail directory: {}",  thumbnailLocalDirectory);
                Files.createDirectories(thumbnailPathObj);
            }
        } catch (IOException e){
            log.error("Error while creating directory", e);
        }
    }

    public void generateThumbnail(UploadedVideoDto uploadedVideo){
        log.info("Generating thumbnail for video: {}", uploadedVideo.getVideoId());
        try{
            Path thumbnailVideoPath = Paths.get(thumbnailLocalDirectory, uploadedVideo.getVideoId().toString());
            if(Files.notExists(thumbnailVideoPath)){
                Files.createDirectories(thumbnailVideoPath);
            }
            Path videoFullPath = uploadedVideo.getVideoPath();
            Path thumbnailFullPath = thumbnailVideoPath.resolve("thumbnail.jpg");

            if(Files.exists(thumbnailFullPath)){
                log.info("Thumbnail already exists for video: {}", uploadedVideo.getVideoId());
                return;
            }

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg",
                    "-ss", "00:00:02",                  // FAST seek (placed before -i)
                    "-i", videoFullPath.toString(), // Input file
                    "-frames:v", "1",                   // Extract exactly 1 video frame
                    "-c:v", "mjpeg",                    // Force the JPEG encoder
                    "-pix_fmt", "yuvj420p",             // CRITICAL: Force standard JPEG color space
                    "-q:v", "2",                        // High quality
                    "-y",                               // Overwrite if exists
                    thumbnailFullPath.toString()            // Output file
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            long pid = process.pid();
            log.info("process: {} started", pid);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("[FFmpeg - thumbnailGenerator] {}", line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Failed to generate thumbnail for video: {}", uploadedVideo.getVideoId());
            } else {
                log.info("Thumbnail generated successfully for video: {}", uploadedVideo.getVideoId());
            }
        } catch (InterruptedException e){
            log.error("Failed to generate thumbnail for video: {}", uploadedVideo.getVideoId(), e);
        } catch (IOException e){
            log.error("Error while uploading thumbnail", e);
        }
    }

    public void processVideo(UploadedVideoDto uploadedVideo) {
        log.info("Processing video: {}", uploadedVideo.getVideoId());

        // Run the orchestration in a virtual thread so the controller returns immediately
        CompletableFuture.runAsync(() -> {
            try {
                Path baseOutputDir = Paths.get(defaultLocalHlsDirectory)
                        .resolve(uploadedVideo.getVideoBasePath());
                if (!Files.exists(baseOutputDir)) {
                    try {
                        Files.createDirectories(baseOutputDir);
                    } catch (IOException e) {
                        log.error("Error creating directory: {}", baseOutputDir, e);
                        return;
                    }
                }

                // 1. Get original video height
                int originalHeight = getVideoHeight(uploadedVideo.getVideoPath());

                // 2. Filter resolutions (Don't upscale a 720p video to 4K)
                List<VideoResolution> targetResolutions = new ArrayList<>();
                for (VideoResolution res : VideoResolution.values()) {
                    if (originalHeight >= res.getHeight()) {
                        targetResolutions.add(res);
                    }
                }

                // 3. Generate Master Playlist upfront
                generateMasterPlaylist(baseOutputDir, targetResolutions);

                // 4. Create parallel tasks for each resolution
                List<CompletableFuture<Void>> futures = new ArrayList<>();

                // 5. Generate thumbnail if not provided
                CompletableFuture<Void> thumbnailFuture = CompletableFuture.runAsync(
                        () -> generateThumbnail(uploadedVideo),
                        virtualThreadExecutor
                );
                futures.add(thumbnailFuture);

                for (VideoResolution resolution : targetResolutions) {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(
                            () -> transcodeResolution(uploadedVideo, resolution),
                            virtualThreadExecutor
                    );
                    futures.add(future);
                }

                // 6. Wait for ALL parallel processes to finish
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                log.info("video: {} processed successfully", uploadedVideo.getVideoId());

                //Delete the original video and folder
                Path originalVideoFolder = Path.of(defaultLocalDirectory).resolve(uploadedVideo.getVideoBasePath());

                try{
                    Files.delete(uploadedVideo.getVideoPath());
                    Files.delete(originalVideoFolder);
                    log.info("deleted original video: {}", originalVideoFolder);
                } catch (IOException e) {
                    log.error("Error deleting original video: {}", originalVideoFolder, e);
                }
            } catch (Exception e) {
                log.error("Error processing video: {}", uploadedVideo, e);
            }
        }, virtualThreadExecutor);
    }

    private void transcodeResolution(UploadedVideoDto uploadedVideo, VideoResolution resolution) {
        final int MAX_RETRIES = 3;
        int noOfTries = 0;

        Path inputPath = uploadedVideo.getVideoPath();
        Path baseOutputDir = Paths.get(defaultLocalHlsDirectory)
                .resolve(uploadedVideo.getVideoBasePath());
        Path resOutputDir = baseOutputDir.resolve(resolution.getName());
        Path outputPath = resOutputDir.resolve("index.m3u8");

        //Creating videoFile of specific resolution and mapping to Video object
        Video video = videoService.findByIdOrThrow(uploadedVideo.getVideoId());
        VideoFile videoFile = VideoFile.builder()
                .resolution(resolution.getHeight())
                .path(outputPath.toString())
                .processingStatus(ProcessingStatus.IN_PROGRESS)
                .video(video)
                .build();
        videoFileService.saveFile(videoFile);

        log.info("videoFile: {}/{} processing to baseOutputDir: {}", uploadedVideo.getVideoId(), resolution.getName(), baseOutputDir);

        while (noOfTries < MAX_RETRIES) {
            log.info("try {}", noOfTries+1);
            try {
                if (!Files.exists(resOutputDir)) Files.createDirectories(resOutputDir);

                // Simple, single-output FFmpeg command
                ProcessBuilder processBuilder = new ProcessBuilder(
                        "ffmpeg",
                        "-i", inputPath.toString(),
                        "-vf", String.format("scale=w=%d:h=%d:force_original_aspect_ratio=decrease", resolution.getWidth(), resolution.getHeight()),
                        "-c:v", "libx264",
                        "-b:v", resolution.getBitrate(),
                        "-maxrate", resolution.getBitrate(),
                        "-bufsize", resolution.getBitrate(), // usually 2x maxrate, keeping simple
                        "-c:a", "aac",
                        "-b:a", "128k",
                        "-hls_time", "10",
                        "-hls_playlist_type", "vod",
                        "-hls_segment_filename", resOutputDir.resolve("segment_%03d.ts").toString(),
                        outputPath.toString()
                );

                // Redirect error stream so we can see ffmpeg logs in console if it fails
                processBuilder.redirectErrorStream(true);

                Process process = processBuilder.start();

                long pid = process.pid();
                log.info("process: {} started", pid);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("[FFmpeg - {}] {}", resolution.getName(), line);
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new RuntimeException("FFmpeg failed for resolution: " + resolution.getName());
                }

                //Update processingStatus for videoFile
                videoFileService.updateVideoFileProcessingStatus(
                        uploadedVideo.getVideoId(),
                        resolution.getHeight(),
                        ProcessingStatus.COMPLETED
                );

                noOfTries++;

                log.info("videoFile: {}/{} processed successfully after {} try(s)",
                        uploadedVideo.getVideoId(),
                        resolution.getName(),
                        noOfTries
                );
                break;
            } catch (Exception e) {
                log.error("FFmpeg failed for resolution: " + resolution.getName(), e);
                noOfTries++;

                if(Files.exists(resOutputDir)) {
                    try {
                        Files.delete(resOutputDir);
                    } catch (IOException ex) {
                        log.error("Failed to cleanup: {}", resOutputDir);
                        break;
                    }
                }
            }
        }

        if(noOfTries == MAX_RETRIES) {
            videoFileService.updateVideoFileProcessingStatus(
                    uploadedVideo.getVideoId(),
                    resolution.getHeight(),
                    ProcessingStatus.FAILED
            );
            log.error("videoFile: {}/{} failed to process", uploadedVideo.getVideoId(), resolution.getName());
        }
    }

    private void generateMasterPlaylist(Path outputDir, List<VideoResolution> resolutions) throws Exception {
        StringBuilder masterPlaylist = new StringBuilder();
        masterPlaylist.append("#EXTM3U\n");
        masterPlaylist.append("#EXT-X-VERSION:3\n");

        for (VideoResolution res : resolutions) {
            // Convert '5000k' to '5000000' for bandwidth mapping
            String bandwidth = res.getBitrate().replace("k", "000");

            masterPlaylist.append(String.format("#EXT-X-STREAM-INF:BANDWIDTH=%s,RESOLUTION=%dx%d\n",
                    bandwidth, res.getWidth(), res.getHeight()));
            masterPlaylist.append(res.getName()).append("/index.m3u8\n");
        }

        Path masterPath = outputDir.resolve("master.m3u8");
        Files.writeString(masterPath, masterPlaylist.toString());

        log.info("masterPlaylist: {} processed successfully", masterPath);
    }

    private int getVideoHeight(Path filePath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "ffprobe", "-v", "error", "-select_streams", "v:0",
                "-show_entries", "stream=height", "-of", "csv=s=x:p=0",
                filePath.toString()
        );
        Process p = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String heightStr = reader.readLine();
        p.waitFor();
        return Integer.parseInt(heightStr.trim());
    }
}
