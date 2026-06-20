package com.akrdev.videostreamingtut.controller;


import com.akrdev.videostreamingtut.dto.jwt.JwtAuthenticationResponse;
import com.akrdev.videostreamingtut.dto.auth.LoginRequest;
import com.akrdev.videostreamingtut.dto.auth.RegisterRequest;
import com.akrdev.videostreamingtut.dto.user.UserDto;
import com.akrdev.videostreamingtut.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Validated RegisterRequest request){
        UserDto dto = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginRequest request){
        JwtAuthenticationResponse response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }
}
