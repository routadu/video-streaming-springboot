package com.akrdev.videostreamingtut.mapper;

import com.akrdev.videostreamingtut.dto.user.UserPublicProfileDto;
import com.akrdev.videostreamingtut.entity.user.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserPublicProfileDtoMapper implements Function<User, UserPublicProfileDto> {
    @Override
    public UserPublicProfileDto apply(User user) {
        return UserPublicProfileDto.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .createdDate(user.getCreatedDate())
                .build();
    }
}
