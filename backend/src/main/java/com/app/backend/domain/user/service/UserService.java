package com.app.backend.domain.user.service;

import com.app.backend.domain.user.dto.request.UserInfoModifyRequest;
import com.app.backend.domain.user.dto.request.UserSignupRequest;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.exception.UserException;
import com.app.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.app.backend.global.error.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(UserSignupRequest req) {
        userRepository
                .findByEmail(req.getEmail())
                .ifPresent(user -> {
                    throw new UserException(EMAIL_DUPLICATION);
                });

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
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserException(USER_NOT_FOUND)
        );
    }

    @Transactional
    public void modifyInfo(User user, UserInfoModifyRequest req) {
        user.modifyInfo(req.getName(), req.getAddress(), req.getDetailAddress(), req.getPhone());
    }

}
