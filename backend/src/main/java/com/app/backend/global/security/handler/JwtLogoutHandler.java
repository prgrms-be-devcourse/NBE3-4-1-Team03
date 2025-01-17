package com.app.backend.global.security.handler;

import com.app.backend.global.redis.repository.RedisRepository;
import com.app.backend.global.security.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtUtil jwtUtil;
    private final RedisRepository redisRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authorization = request.getHeader("Authorization");
        String accessToken = null;

        if (authorization != null) {
            accessToken = authorization.substring(7);
        }

        if(!jwtUtil.isExpired(accessToken)) {
            String username = jwtUtil.getUsername(accessToken);
            redisRepository.delete(username);
        }

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
