package com.akrdev.videostreamingtut.mapper;

import com.akrdev.videostreamingtut.dto.user.RegisterRequest;
import com.akrdev.videostreamingtut.entity.user.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class RegisterRequestUserMapper implements Function<RegisterRequest, User> {
    @Override
    public User apply(RegisterRequest registerRequest) {
        return User.builder()
                .username(registerRequest.getUsername())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .role(registerRequest.getRoles())
                .build();
    }
}
