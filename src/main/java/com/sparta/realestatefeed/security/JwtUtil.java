package com.sparta.realestatefeed.security;

import com.sparta.realestatefeed.config.JwtConfig;
import com.sparta.realestatefeed.entity.UserRoleEnum;
import com.sparta.realestatefeed.repository.user.UserRepository;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    private final UserRepository userRepository;

    public JwtUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String createAccessToken(String username, UserRoleEnum role) {

        Date date = new Date();

        return JwtConfig.BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)
                        .claim(JwtConfig.AUTHORIZATION_KEY, role)
                        .setExpiration(new Date(date.getTime() + JwtConfig.ACCESS_TOKEN_TIME))
                        .setIssuedAt(date)
                        .signWith(JwtConfig.key, JwtConfig.SIGNATURE_ALGORITHM)
                        .compact();
    }

    public String createRefreshToken(String username) {

        Date date = new Date();

        return JwtConfig.BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)
                        .setExpiration(new Date(date.getTime() + JwtConfig.REFRESH_TOKEN_TIME))
                        .setIssuedAt(date)
                        .signWith(JwtConfig.key)
                        .compact();
    }

    public String getAccessTokenFromHeader(HttpServletRequest request) {

        String bearerToken = request.getHeader(JwtConfig.ACCESS_TOKEN_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConfig.BEARER_PREFIX)) {
            return bearerToken.replace(JwtConfig.BEARER_PREFIX, "");
        }

        return null;
    }

    public String getRefreshTokenFromHeader(HttpServletRequest request) {

        String bearerToken = request.getHeader(JwtConfig.REFRESH_TOKEN_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConfig.BEARER_PREFIX)) {
            return bearerToken.replace(JwtConfig.BEARER_PREFIX, "");
        }
        return null;
    }

    public boolean validateToken(String token) {

        try {
            Jwts.parserBuilder().setSigningKey(JwtConfig.key).build().parseClaimsJws(token);
            return true;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            return false;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    public Claims getUserInfoFromToken(String token) {

        return Jwts.parserBuilder().setSigningKey(JwtConfig.key).build().parseClaimsJws(token).getBody();
    }
}
