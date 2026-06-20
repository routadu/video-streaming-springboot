package com.akrdev.videostreamingtut.service.user;


import com.akrdev.videostreamingtut.dto.jwt.JwtAuthenticationResponse;
import com.akrdev.videostreamingtut.dto.auth.LoginRequest;
import com.akrdev.videostreamingtut.dto.auth.RegisterRequest;
import com.akrdev.videostreamingtut.dto.user.UserDto;
import com.akrdev.videostreamingtut.dto.user.UserPublicProfileDto;
import com.akrdev.videostreamingtut.entity.user.User;
import com.akrdev.videostreamingtut.exception.UserAlreadyExistsException;
import com.akrdev.videostreamingtut.exception.UserNotFoundException;

import java.util.Optional;

public interface UserService {
    User registerUser(User user) throws UserAlreadyExistsException;
    UserDto registerUser(RegisterRequest request) throws UserAlreadyExistsException;
    JwtAuthenticationResponse loginUser(LoginRequest request);
    Optional<User> findByUsername(String id);
    User findByUsernameOrThrow(String id) throws UserNotFoundException;
    UserDto findUserDtoByUsernameOrThrow(String username) throws UserNotFoundException;
    UserPublicProfileDto findUserPublicProfileDtoByUsernameOrThrow(String username) throws UserNotFoundException;
    Optional<User> findByEmail(String email);
    User findByEmailOrThrow(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    User findByUsernameOrEmailOrThrow(String username, String email);

}
