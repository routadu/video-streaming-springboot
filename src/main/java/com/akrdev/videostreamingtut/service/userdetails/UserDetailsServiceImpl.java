package com.akrdev.videostreamingtut.service.userdetails;

import com.akrdev.videostreamingtut.entity.user.User;
import com.akrdev.videostreamingtut.entity.user.UserDetailsImpl;
import com.akrdev.videostreamingtut.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserService  userService;

    @Autowired
    @Lazy
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.loadUserByUsername(username);
        return UserDetailsImpl.fromUser(user);
    }
}
