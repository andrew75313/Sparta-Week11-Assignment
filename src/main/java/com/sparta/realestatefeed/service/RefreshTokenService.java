package com.sparta.realestatefeed.service;

import com.sparta.realestatefeed.config.JwtConfig;
import com.sparta.realestatefeed.entity.RefreshToken;
import com.sparta.realestatefeed.entity.User;
import com.sparta.realestatefeed.exception.UserNotFoundException;
import com.sparta.realestatefeed.repository.RefreshTokenRepository;
import com.sparta.realestatefeed.repository.user.UserRepository;
import com.sparta.realestatefeed.security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtUtil.getRefreshTokenFromHeader(request);
        Claims userInfo = jwtUtil.getUserInfoFromToken(refreshToken);

        User foundUser = userRepository.findByUserName(userInfo.getSubject()).orElseThrow(
                () -> new UserNotFoundException("사용자를 찾을 수 없습니다.")
        );

        RefreshToken foundToken = refreshTokenRepository.findByUserId(foundUser.getId()).orElseThrow(
                () -> new NoSuchElementException("토큰을 찾을 수 없습니다.")
        );

        if (!refreshToken.equals(foundToken.getToken().replace(JwtConfig.BEARER_PREFIX, ""))) {
            throw new NoSuchElementException("재로그인을 해주세요.");
        }

        String newAccessToken = jwtUtil.createAccessToken(foundUser.getUserName(), foundUser.getRole());

        response.addHeader(JwtConfig.ACCESS_TOKEN_HEADER, newAccessToken);

        return ResponseEntity.status(HttpStatus.OK).body("토큰 발급을 완료했습니다.");
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUserId(user.getId());
    }
}
