package com.app.backend.global.security.util;

import com.app.backend.global.security.user.CustomUserDetails;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS512.key().build().getAlgorithm());
    }

    public Long getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("id", Long.class);
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createAccessToken(CustomUserDetails customUserDetails, long expiration) {
        long currentTime = System.currentTimeMillis();

        return "Bearer " + Jwts.builder()
                .claim("subject", "access")
                .claim("id", customUserDetails.getUser().getId())
                .claim("username", customUserDetails.getUsername())
                .claim("role", customUserDetails.getUser().getRole())
                .issuedAt(new Date(currentTime))
                .expiration(new Date(currentTime + expiration))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(CustomUserDetails customUserDetails, long expiration) {
        long currentTime = System.currentTimeMillis();

        return Jwts.builder()
                .claim("subject", "refresh")
                .claim("id", customUserDetails.getUser().getId())
                .claim("username", customUserDetails.getUsername())
                .claim("role", customUserDetails.getUser().getRole())
                .issuedAt(new Date(currentTime))
                .expiration(new Date(currentTime + expiration))
                .signWith(secretKey)
                .compact();
    }

    public Cookie setJwtCookie(String key, String value, long expiration) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int) expiration / 1000);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
