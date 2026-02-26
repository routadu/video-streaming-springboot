package com.akrdev.videostreamingtut.service.video;

import com.akrdev.videostreamingtut.dto.video.VideoListDto;
import com.akrdev.videostreamingtut.dto.video.VideoDto;
import com.akrdev.videostreamingtut.entity.video.Video;
import com.akrdev.videostreamingtut.exception.VideoNotFoundException;
import com.akrdev.videostreamingtut.mapper.VideoDtoMapper;
import com.akrdev.videostreamingtut.mapper.VideoDtoMapperWithResolution;
import com.akrdev.videostreamingtut.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final VideoDtoMapper videoDtoMapper;
    private final VideoDtoMapperWithResolution  videoDtoMapperWithResolution;

    @Value("${video.default-local-hls-directory}")
    private String defaultLocalHlsDirectory;

    @Value("${thumbnail.default-local-directory}")
    private String defaultThumbnailDirectory;

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

    @Override
    public VideoDto findDtoByIdOrThrow(UUID videoId) throws VideoNotFoundException {
        return videoDtoMapperWithResolution.apply(findByIdOrThrow(videoId));
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

    @Override
    public VideoListDto findAllByQuery(String query) {
        List<VideoDto> dtoList = videoRepository.findByQuery(query)
                .stream()
                .map(videoDtoMapper)
                .toList();
        return VideoListDto.builder()
                .videos(dtoList)
                .build();
    }

    @Override
    public VideoListDto findAllByUsername(String username) {
        List<VideoDto> dtoList = videoRepository.findByOwnerUsername(username)
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

    @Override
    public Resource getThumbnailFile(UUID videoId) {
        Path path = Paths.get(defaultThumbnailDirectory, videoId.toString(), "thumbnail.jpg");
        if(Files.notExists(path)) {
            return new ByteArrayResource(new byte[0]);
        }
        return new FileSystemResource(path);
    }
}
