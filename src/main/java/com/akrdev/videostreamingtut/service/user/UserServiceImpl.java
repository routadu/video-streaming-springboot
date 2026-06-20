package com.akrdev.videostreamingtut.service.user;

import com.akrdev.videostreamingtut.dto.user.UserDto;
import com.akrdev.videostreamingtut.dto.user.UserPublicProfileDto;
import com.akrdev.videostreamingtut.entity.user.User;
import com.akrdev.videostreamingtut.exception.user.UserNotFoundException;
import com.akrdev.videostreamingtut.mapper.UserDtoMapper;
import com.akrdev.videostreamingtut.mapper.UserPublicProfileDtoMapper;
import com.akrdev.videostreamingtut.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserDtoMapper userDtoMapper;
    private final UserPublicProfileDtoMapper  userPublicProfileDtoMapper;

    @Override
    public User saveUser(User user) {
        if(user == null)
            throw new UserNotFoundException("User not found");
        return repository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findById(username);
    }

    @Override
    public User findByUsernameOrThrow(String username) throws UserNotFoundException {
        return findByUsername(username)
                .orElseThrow(() -> UserNotFoundException.id(username));
    }

    @Override
    public UserDto findUserDtoByUsernameOrThrow(String username) throws UserNotFoundException {
        return userDtoMapper.apply(findByUsernameOrThrow(username));
    }

    @Override
    public UserPublicProfileDto findUserPublicProfileDtoByUsernameOrThrow(String username) throws UserNotFoundException {
        return userPublicProfileDtoMapper.apply(findByUsernameOrThrow(username));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public User findByEmailOrThrow(String email) {
        return findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.email(email));
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return repository.findByUsernameOrEmail(username, email);
    }

    @Override
    public User findByUsernameOrEmailOrThrow(String username, String email) {
        return findByUsernameOrEmail(username, email)
                .orElseThrow(() -> UserNotFoundException.usernameOrEmail(username, email));
    }
}
