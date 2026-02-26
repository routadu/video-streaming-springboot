package com.akrdev.videostreamingtut.repository;

import com.akrdev.videostreamingtut.entity.video.continuewatching.ContinueWatching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContinueWatchingRepository extends JpaRepository<ContinueWatching, String> {
}
