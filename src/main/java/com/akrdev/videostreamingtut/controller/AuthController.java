package com.akrdev.videostreamingtut.controller;


import com.akrdev.videostreamingtut.data.dto.jwt.JwtAuthenticationResponse;
import com.akrdev.videostreamingtut.data.dto.auth.LoginRequest;
import com.akrdev.videostreamingtut.data.dto.auth.RegisterRequest;
import com.akrdev.videostreamingtut.data.dto.user.UserDto;
import com.akrdev.videostreamingtut.service.auth.AuthService;
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

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Validated RegisterRequest request){
        UserDto dto = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginRequest request){
        JwtAuthenticationResponse response = authService.loginUser(request);
        return ResponseEntity.ok(response);
    }
}
