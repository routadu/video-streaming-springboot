package com.akrdev.videostreamingtut.controller;

import com.akrdev.videostreamingtut.dto.user.UserPublicProfileDto;
import com.akrdev.videostreamingtut.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<Map<String, UserPublicProfileDto>> getUserPublicProfile(@PathVariable String username) {
        UserPublicProfileDto dto = userService.findUserPublicProfileDtoByUsernameOrThrow(username);
        return ResponseEntity.ok()
                .body(Map.of("user", dto));
    }
}
