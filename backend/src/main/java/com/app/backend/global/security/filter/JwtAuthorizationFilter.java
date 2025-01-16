package com.app.backend.global.security.filter;

import com.app.backend.domain.user.entity.User;
import com.app.backend.global.security.util.JwtUtil;
import com.app.backend.global.security.user.CustomUserDetails;
import com.app.backend.global.security.util.AuthResponseUtil;
import com.app.backend.global.redis.repository.RedisRepository;
import com.app.backend.global.rs.RsData;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final long ACCESS_EXPIRATION;
    private final long REFRESH_EXPIRATION;
    private final ObjectMapper objectMapper;
    private final RedisRepository redisRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (requestURI.equals("/api/v1/reissue")) {
            reissueFilter(request, response);
        } else {
            accessFilter(request, response, filterChain);
        }
    }

    private void reissueFilter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String refreshToken = getRefreshToken(request);

        if (refreshToken == null) {
            AuthResponseUtil.failLogin(
                    response,
                    new RsData<>(false, "400", "refreshToken 이 존재하지 않습니다"),
                    HttpServletResponse.SC_BAD_REQUEST,
                    objectMapper);
            return;
        }

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // Todo : 리프레시 저장 key 값 정하기
        if (!redisRepository.get(username).equals(refreshToken)) {
            // Todo : refreshToken 이 불일치 에러
            AuthResponseUtil.failLogin(
                    response,
                    new RsData<>(false, "400", "잘못된 refreshToken"),
                    HttpServletResponse.SC_BAD_REQUEST,
                    objectMapper
            );
            return;
        }

        CustomUserDetails userDetails = new CustomUserDetails(User.builder().email(username).role(role).build());

        String newAccessToken = jwtUtil.createAccessToken(userDetails, ACCESS_EXPIRATION);
        String newRefreshToken = jwtUtil.createRefreshToken(userDetails, REFRESH_EXPIRATION);

        redisRepository.delete(userDetails.getUsername());
        redisRepository.save(userDetails.getUsername(), newRefreshToken, REFRESH_EXPIRATION, TimeUnit.MILLISECONDS);

        AuthResponseUtil.success(
                response,
                newAccessToken,
                jwtUtil.setJwtCookie("refreshToken", newRefreshToken, REFRESH_EXPIRATION),
                HttpServletResponse.SC_OK,
                new RsData<>(true, "200", "AccessToken 재발급 성공", userDetails.getUsername()),
                objectMapper);
    }

    private void accessFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authorization.substring(7);

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            // Todo : accessToken 이 만료되면 오류 반환 (임시 코드 설정)
            AuthResponseUtil.failLogin(
                    response,
                    new RsData<>(false, "401", "accessToken 의 유효기간이 만료되었습니다"),
                    HttpServletResponse.SC_UNAUTHORIZED,
                    objectMapper);
            return;
        } catch (JwtException e) {
            // Todo : 이외 오류 반환 (임시 코드 설정)
            AuthResponseUtil.failLogin(
                    response,
                    new RsData<>(false, "400", "accessToken 의 내용을 확인할 수 없습니다"),
                    HttpServletResponse.SC_BAD_REQUEST,
                    objectMapper);
            return;
        }

        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        CustomUserDetails userDetails = new CustomUserDetails(User.builder().email(username).role(role).build());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    private String getRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        String refreshToken = null;

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
            }
        }

        return refreshToken;
    }
}
