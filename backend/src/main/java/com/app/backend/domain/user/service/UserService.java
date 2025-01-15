package com.app.backend.domain.user.service;

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

    public User signup(
            String email,
            String password,
            String name,
            String address,
            String detailAddress,
            String phone
    ) {
        userRepository
                .findByEmail(email)
                .ifPresent(user -> {
                    throw new UserException(EMAIL_DUPLICATION);
                });

        if (address == null || address.trim().isEmpty()) {
            throw new UserException(INVALID_INPUT_VALUE);
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .address(address)
                .detailAddress(detailAddress)
                .phone(phone)
                .status("ACTIVATED")
                .role("ROLE_USER")
                .build();

        return userRepository.save(user);
    }

}
