package com.app.backend.global.security.util;

import com.app.backend.domain.user.entity.User;
import com.app.backend.global.security.user.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.token.access-expiration}")
    private long ACCESS_EXPIRATION;

    @Value("${jwt.token.refresh-expiration}")
    private long REFRESH_EXPIRATION;

    @Test
    @DisplayName("AccessToken 생성 & 검증")
    void testCreateAccessToken() {
        CustomUserDetails userDetails = new CustomUserDetails(
                User.builder().email("test@example.com").role("USER").build());

        String accessToken = jwtUtil.createAccessToken(userDetails, ACCESS_EXPIRATION).substring(7);
        assertNotNull(accessToken);

        String username = jwtUtil.getUsername(accessToken);
        assertEquals("test@example.com", username);

        String role = jwtUtil.getRole(accessToken);
        assertEquals("USER", role);
    }

    @Test
    @DisplayName("RefreshToken 생성 & 검증")
    void testCreateRefreshToken() {
        CustomUserDetails userDetails = new CustomUserDetails(
                User.builder().email("test@example.com").role("USER").build());

        String refreshToken = jwtUtil.createRefreshToken(userDetails, REFRESH_EXPIRATION);
        assertNotNull(refreshToken);

        String username = jwtUtil.getUsername(refreshToken);
        assertEquals("test@example.com", username);

        String role = jwtUtil.getRole(refreshToken);
        assertEquals("USER", role);
    }

    @Test
    @DisplayName("만료된 AccessToken 검증")
    void testIsExpired() {
        CustomUserDetails userDetails = new CustomUserDetails(
                User.builder().email("test@example.com").role("USER").build());

        String expiredToken = jwtUtil.createAccessToken(userDetails, -1000).substring(7);
        assertThrows(ExpiredJwtException.class, () -> jwtUtil.isExpired(expiredToken));
    }
}