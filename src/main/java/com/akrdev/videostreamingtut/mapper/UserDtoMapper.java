package com.akrdev.videostreamingtut.mapper;

import com.akrdev.videostreamingtut.dto.user.UserDto;
import com.akrdev.videostreamingtut.entity.user.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserDtoMapper implements Function<User, UserDto> {
    @Override
    public UserDto apply(User user) {
        return UserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
