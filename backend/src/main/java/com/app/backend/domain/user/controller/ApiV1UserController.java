package com.app.backend.domain.user.controller;

import com.app.backend.domain.order.dto.response.OrderResponse;
import com.app.backend.domain.order.service.OrderService;
import com.app.backend.domain.user.dto.request.UserChangePasswordRequest;
import com.app.backend.domain.user.dto.request.UserInfoModifyRequest;
import com.app.backend.domain.user.dto.request.UserSignupRequest;
import com.app.backend.domain.user.dto.response.UserInfoResponse;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.exception.UserException;
import com.app.backend.domain.user.service.UserService;
import com.app.backend.global.error.exception.ErrorCode;
import com.app.backend.global.rq.Rq;
import com.app.backend.global.rs.RsData;
import com.app.backend.global.security.dto.LoginDto;
import com.app.backend.global.security.user.CustomUserDetails;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.app.backend.global.error.exception.ErrorCode.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ApiV1UserController {

    private final UserService  userService;
    private final OrderService orderService;

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
    public RsData<UserInfoResponse> getUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userService.getUserById(userId);

        if (userDetails == null) {
            throw new UserException(UNAUTHENTICATION_USER);
        }

        if (!Objects.equals(userId, userDetails.getUser().getId())) {
            throw new UserException(UNAUTHORIZATION_USER);
        }

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
            @Valid @RequestBody UserInfoModifyRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userService.getUserById(userId);

        if (userDetails == null) {
            throw new UserException(UNAUTHENTICATION_USER);
        }

        if (!userId.equals(userDetails.getUser().getId())) {
            throw new UserException(UNAUTHORIZATION_USER);
        }

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
            @Valid @RequestBody UserChangePasswordRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userService.getUserById(userId);

        if (userDetails == null) {
            throw new UserException(UNAUTHENTICATION_USER);
        }

        if (!userId.equals(userDetails.getUser().getId())) {
            throw new UserException(UNAUTHORIZATION_USER);
        }

        userService.changePassword(user, req);

        return new RsData<>(
                true,
                "200",
                "비밀번호를 성공적으로 변경하였습니다."
        );
    }

    @DeleteMapping("/users/{userId}")
    @Transactional
    public RsData<Void> deleteUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userService.getUserById(userId);

        if (userDetails == null) {
            throw new UserException(UNAUTHENTICATION_USER);
        }

        if (!userId.equals(userDetails.getUser().getId())) {
            throw new UserException(UNAUTHORIZATION_USER);
        }

        userService.deleteUser(user);

        return new RsData<>(
                true,
                "200",
                "탈퇴가 성공적으로 이루어졌습니다."
        );
    }

    @GetMapping("/users/orders")
    public RsData<List<OrderResponse>> getOrdersByUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();

        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);

        return new RsData<>(true,
                            String.valueOf(HttpStatus.OK.value()),
                            "회원의 주문 정보를 성공적으로 조회했습니다.",
                            orders);
    }

}
