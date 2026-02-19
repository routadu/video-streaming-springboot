package com.akrdev.videostreamingtut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VideoStreamingTutApplication {

    static void main(String[] args) {
        SpringApplication.run(VideoStreamingTutApplication.class, args);
    }

}
