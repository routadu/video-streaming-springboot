package com.akrdev.videostreamingtut.entity.video;


import com.akrdev.videostreamingtut.entity.video.videofile.VideoFile;
import com.akrdev.videostreamingtut.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    private String contentType;

    private Integer duration;

    private String thumbnail;

    @CreationTimestamp
    private LocalDateTime uploadTimestamp;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_username")
    private User owner;

    @ToString.Exclude
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoFile> videos;

    public Path getVideoIdPath(){
        return Path.of(id.toString());
    }

    public Path getMasterFilePath(){
        return getVideoIdPath().resolve("master.m3u8");
    }
}
