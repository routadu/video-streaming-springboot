package com.akrdev.videostreamingtut.entity.user;

import com.akrdev.videostreamingtut.entity.video.Video;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, columnDefinition = "varchar(30) default 'ROLE_USER'")
    private String role;

    @ToString.Exclude
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<Video> videoList;
}
