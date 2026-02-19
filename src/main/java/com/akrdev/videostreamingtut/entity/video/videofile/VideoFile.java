package com.akrdev.videostreamingtut.entity.video.videofile;

import com.akrdev.videostreamingtut.entity.video.Video;
import com.akrdev.videostreamingtut.entity.video.videofile.processingstatus.ProcessingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.nio.file.Path;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "video_files",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"video_id", "resolution"})
        }
)
public class VideoFile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private int resolution;

    private String path;

    @Enumerated(EnumType.STRING)
    private ProcessingStatus processingStatus;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    // This column is not inserted in the database, but Hibernate references it from the video_id of Video
    // tracking all changes. No need to fetch Video object just to get the videoId
    @Column(name = "video_id", insertable = false, updatable = false)
    private UUID videoId;
}
