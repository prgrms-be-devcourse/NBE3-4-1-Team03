package com.app.backend.domain.user.controller;

import com.app.backend.domain.user.dto.request.UserChangePasswordRequest;
import com.app.backend.domain.user.dto.request.UserInfoModifyRequest;
import com.app.backend.domain.user.dto.request.UserSignupRequest;
import com.app.backend.domain.user.dto.response.UserInfoResponse;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.service.UserService;
import com.app.backend.global.rq.Rq;
import com.app.backend.global.rs.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ApiV1UserController {

    private final UserService userService;
    private final Rq rq;

    @PostMapping("/signup")
    @Transactional
    public RsData<Void> signup(@Valid @RequestBody UserSignupRequest req) {
        userService.signup(req);

        return new RsData<>(
                true,
                "201",
                "%s님, 회원가입을 축하합니다.".formatted(req.getName())
        );
    }

    @GetMapping("/users/{userId}")
    @Transactional(readOnly = true)
    public RsData<UserInfoResponse> getUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);

        // TODO (rq로 회원인증 후 동작하도록 구현)

        return new RsData<>(
                true,
                "200",
                "회원 정보를 성공적으로 불러왔습니다.",
                new UserInfoResponse(user)
        );
    }

    @PatchMapping("/users/{userId}")
    @Transactional
    public RsData<Void> modifyUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserInfoModifyRequest req
    ) {
        User user = userService.getUserById(userId);

        // TODO (rq로 회원인증 후 동작하도록 구현)

        userService.modifyInfo(user, req);

        return new RsData<>(
                true,
                "200",
                "회원 정보를 성공적으로 수정하였습니다."
        );
    }

    @PatchMapping("/users/{userId}/password")
    @Transactional
    public RsData<Void> changePassword(
        @PathVariable Long userId,
        @Valid @RequestBody UserChangePasswordRequest req
    ) {
        User user = userService.getUserById(userId);

        // TODO (rq로 회원인증 후 동작하도록 구현)

        userService.changePassword(user, req);

        return new RsData<>(
                true,
                "200",
                "비밀번호를 성공적으로 변경하였습니다."
        );
    }

    @DeleteMapping("/users/{userId}")
    @Transactional
    public RsData<Void> deleteUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);

        // TODO (rq로 회원인증 후 동작하도록 구현)

        userService.deleteUser(user);

        return new RsData<>(
                true,
                "200",
                "탈퇴가 성공적으로 이루어졌습니다."
        );
    }

}
