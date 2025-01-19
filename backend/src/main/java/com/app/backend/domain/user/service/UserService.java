package com.app.backend.domain.user.service;

import com.app.backend.domain.user.dto.request.UserChangePasswordRequest;
import com.app.backend.domain.user.dto.request.UserInfoModifyRequest;
import com.app.backend.domain.user.dto.request.UserSignupRequest;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.entity.UserRole;
import com.app.backend.domain.user.entity.UserStatus;
import com.app.backend.domain.user.exception.UserException;
import com.app.backend.domain.user.repository.UserRepository;
import com.app.backend.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
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
                .status(UserStatus.getDefaultStatus().toString())
                .role(UserRole.getDefaultRole().toString())
                .build();

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserException(USER_NOT_FOUND)
        );

        if (user.getStatus().equals(UserStatus.DELETED.toString())) {
            throw new UserException(USER_DELETED);
        }

        return user;
    }

    @Transactional
    public void modifyInfo(User user, UserInfoModifyRequest req) {
        user.modifyInfo(req.getName(), req.getAddress(), req.getDetailAddress(), req.getPhone());
    }

    public void changePassword(User user, UserChangePasswordRequest req) {
        if (!user.getEmail().equals(req.getEmail())) {
            throw new UserException(INVALID_INPUT_VALUE);
        }

        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword())) {
            throw new UserException(PASSWORD_SAME_AS_CURRENT);
        }

        user.changePassword(passwordEncoder.encode(req.getNewPassword()));
    }

    public void deleteUser(User user) {
        user.deleteUser();
    }

    public void isAdmin(UserDetails userDetails) {
        if (!userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")))
            throw new UserException(ErrorCode.HANDLE_ACCESS_DENIED);
    }
}
