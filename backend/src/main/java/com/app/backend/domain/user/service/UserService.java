package com.app.backend.domain.user.service;

import com.app.backend.domain.user.dto.request.UserSignupRequest;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.exception.UserException;
import com.app.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.app.backend.global.error.exception.ErrorCode.EMAIL_DUPLICATION;
import static com.app.backend.global.error.exception.ErrorCode.INVALID_INPUT_VALUE;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User signup(UserSignupRequest req) {
        userRepository
                .findByEmail(req.getEmail())
                .ifPresent(user -> {
                    throw new UserException(EMAIL_DUPLICATION);
                });

        if (req.getAddress() == null || req.getAddress().trim().isEmpty()) {
            throw new UserException(INVALID_INPUT_VALUE);
        }

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .address(req.getAddress())
                .detailAddress(req.getDetailAddress())
                .phone(req.getPhone())
                .status("ACTIVATED")
                .role("ROLE_USER")
                .build();

        userRepository.save(user);

        return user;
    }

}
