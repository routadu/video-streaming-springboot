package com.akrdev.videostreamingtut.service.dependencycheck;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class FFmpegDependencyChecker {

    private final ApplicationContext applicationContext;

    public FFmpegDependencyChecker(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void checkDependencies() {
        log.info("Dependency check: FFmpeg / FFprobe...");

        boolean ffmpegAvailable = isCommandAvailable("ffmpeg", "-version");
        boolean ffprobeAvailable = isCommandAvailable("ffprobe", "-version");

        if (!ffmpegAvailable || !ffprobeAvailable) {
            log.error("\n=========================================================================");
            log.error("DEPENDENCIES MISSING: FFmpeg / FFprobe");
            log.error("Install these required dependencies to run the application");
            log.error("=========================================================================\n");

            int exitCode = SpringApplication.exit(applicationContext, () -> 1);
            System.exit(exitCode);
        } else {
            log.info("FFmpeg and FFprobe installed");
        }
    }

    private boolean isCommandAvailable(String... command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            process.waitFor();
            return true;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
