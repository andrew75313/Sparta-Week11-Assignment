package com.sparta.realestatefeed.service;

import com.sparta.realestatefeed.entity.User;
import com.sparta.realestatefeed.repository.user.UserRepository;
import com.sparta.realestatefeed.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 RefreshTokenService refreshTokenService,
                                 JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
    }


    @Transactional
    public void logoutUser(String token, String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        refreshTokenService.deleteByUser(user);

    }
}
