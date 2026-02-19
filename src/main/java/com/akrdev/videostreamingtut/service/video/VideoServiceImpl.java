package com.akrdev.videostreamingtut.service.video;

import com.akrdev.videostreamingtut.dto.video.VideoListDto;
import com.akrdev.videostreamingtut.dto.video.VideoDto;
import com.akrdev.videostreamingtut.entity.video.Video;
import com.akrdev.videostreamingtut.exception.VideoNotFoundException;
import com.akrdev.videostreamingtut.mapper.VideoDtoMapper;
import com.akrdev.videostreamingtut.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final VideoDtoMapper videoDtoMapper;

    @Value("${video.default-local-hls-directory}")
    private String defaultLocalHlsDirectory;

    public Video saveVideo(Video video) {
        return  videoRepository.save(video);
    }

    public Optional<Video> findById(UUID videoId) {
        return videoRepository.findById(videoId);
    }

    public Video findByIdOrThrow(UUID videoId) throws VideoNotFoundException {
        return findById(videoId)
                .orElseThrow(() -> new VideoNotFoundException(videoId));
    }

    public VideoListDto findAllVideos() {
        List<VideoDto> dtoList = videoRepository.findAll()
                .stream()
                .map(videoDtoMapper)
                .toList();
        return VideoListDto.builder()
                .videos(dtoList)
                .build();
    }

    public Resource getMasterFile(UUID videoId){
        Video video = findByIdOrThrow(videoId);
        Path resourcePath = Path.of(defaultLocalHlsDirectory).resolve(video.getMasterFilePath());
        return new FileSystemResource(resourcePath);
    }
}
