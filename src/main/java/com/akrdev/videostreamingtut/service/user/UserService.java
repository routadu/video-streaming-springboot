package com.akrdev.videostreamingtut.service.user;


import com.akrdev.videostreamingtut.dto.user.UserDto;
import com.akrdev.videostreamingtut.dto.user.UserPublicProfileDto;
import com.akrdev.videostreamingtut.entity.user.User;
import com.akrdev.videostreamingtut.exception.user.UserNotFoundException;

import java.util.Optional;

public interface UserService {
    User saveUser(User user);
    Optional<User> findByUsername(String id);
    User findByUsernameOrThrow(String id) throws UserNotFoundException;
    UserDto findUserDtoByUsernameOrThrow(String username) throws UserNotFoundException;
    UserPublicProfileDto findUserPublicProfileDtoByUsernameOrThrow(String username) throws UserNotFoundException;
    Optional<User> findByEmail(String email);
    User findByEmailOrThrow(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    User findByUsernameOrEmailOrThrow(String username, String email);

}
