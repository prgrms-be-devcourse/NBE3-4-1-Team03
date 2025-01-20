package com.app.backend.global.security.filter;

import com.app.backend.domain.user.exception.UserException;
import com.app.backend.global.security.dto.LoginDto;
import com.app.backend.global.security.util.JwtUtil;
import com.app.backend.global.security.user.CustomUserDetails;
import com.app.backend.global.security.util.AuthResponseUtil;
import com.app.backend.global.redis.repository.RedisRepository;
import com.app.backend.global.rs.RsData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.app.backend.global.error.exception.ErrorCode.INVALID_INPUT_VALUE;

// setFilterProcessesUrl("/api/login") 요청 url
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final long ACCESS_EXPIRATION;
    private final long REFRESH_EXPIRATION;
    private final ObjectMapper objectMapper;
    private final RedisRepository redisRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginDto.LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginDto.LoginRequest.class);

            validateDto(response,loginRequest);

            String username = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new AuthenticationServiceException("로그인 정보가 정확하지 않습니다");
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        AuthResponseUtil.failLogin(
                response,
                new RsData<>(false, "400", "로그인 정보가 정확하지 않습니다"),
                HttpServletResponse.SC_BAD_REQUEST,
                objectMapper);
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtUtil.createAccessToken(userDetails, ACCESS_EXPIRATION);
        String refreshToken = jwtUtil.createRefreshToken(userDetails, REFRESH_EXPIRATION);

        // Todo: refreshToken key 정하기 username 대체

        redisRepository.delete(userDetails.getUsername());
        redisRepository.save(userDetails.getUsername(), refreshToken, REFRESH_EXPIRATION, TimeUnit.MILLISECONDS);

        AuthResponseUtil.success(
                response,
                accessToken,
                jwtUtil.setJwtCookie("refreshToken", refreshToken, REFRESH_EXPIRATION),
                HttpServletResponse.SC_OK,
                new RsData<>(true, "200", "로그인 성공", new LoginDto.LoginResponse(userDetails.getUsername())),
                objectMapper);
    }

    private void validateDto(HttpServletResponse response, LoginDto.LoginRequest loginRequest)throws IOException {
        Set<ConstraintViolation<LoginDto.LoginRequest>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(loginRequest);

        if(!violations.isEmpty()) {
//            String errorMessage = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", ")); // dto 에 설정된 메세지 사용시 해제
            throw new UserException(INVALID_INPUT_VALUE);
        }
    }
}
