package com.app.backend.domain.user.controller;

import com.app.backend.domain.user.dto.request.UserSignupRequest;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.service.UserService;
import com.app.backend.global.rq.Rq;
import com.app.backend.global.rs.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                    "%s님, 회원가입을 축하합니다.".formatted(req.getName()),
                    "201"
                );
    }

}
