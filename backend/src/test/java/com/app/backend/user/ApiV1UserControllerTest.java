package com.app.backend.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.app.backend.domain.order.dto.response.OrderResponse;
import com.app.backend.domain.order.entity.Order;
import com.app.backend.domain.order.service.OrderService;
import com.app.backend.domain.user.controller.ApiV1UserController;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.entity.UserStatus;
import com.app.backend.domain.user.repository.UserRepository;
import com.app.backend.global.annotation.CustomWithMockAdmin;
import com.app.backend.global.util.ReflectionUtil;
import com.app.backend.standard.util.Ut;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ApiV1UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private OrderService orderService;

    @Test
    @DisplayName("회원가입")
    void signupTest1() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        post("/api/v1/signup")
                                .content("""
                                         {
                                             "email": "test@test.com",
                                             "password": "test1234!",
                                             "name": "test1",
                                             "address": "address",
                                             "detailAddress": "detailAddress",
                                             "phone": "01012345678"
                                         }
                                         """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("signup"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value("test1님, 회원가입을 축하합니다."))
                .andExpect(jsonPath("$.code").value("201"));
    }

    @Test
    @DisplayName("회원가입 - 잘못된 이메일 형식")
    void signupTest2() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        post("/api/v1/signup")
                                .content("""
                                         {
                                             "email": "test@.com",
                                             "password": "test1234!",
                                             "name": "test1",
                                             "address": "address",
                                             "detailAddress": "detailAddress",
                                             "phone": "01012345678"
                                         }
                                         """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("signup"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값"))
                .andExpect(jsonPath("$.code").value("C001"));
    }

    @Test
    @DisplayName("회원가입 - 비밀번호 형식 불일치 (특수문자 누락)")
    void signupTest3() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        post("/api/v1/signup")
                                .content("""
                                         {
                                             "email": "test@test.com",
                                             "password": "test1234",
                                             "name": "test1",
                                             "address": "address",
                                             "detailAddress": "detailAddress",
                                             "phone": "01012345678"
                                         }
                                         """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("signup"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값"))
                .andExpect(jsonPath("$.code").value("C001"));
    }

    @Test
    @DisplayName("회원가입 - 비밀번호 형식 불일치 (8글자 미만)")
    void signupTest4() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        post("/api/v1/signup")
                                .content("""
                                         {
                                             "email": "test@test.com",
                                             "password": "test1!",
                                             "name": "test1",
                                             "address": "address",
                                             "detailAddress": "detailAddress",
                                             "phone": "01012345678"
                                         }
                                         """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("signup"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값"))
                .andExpect(jsonPath("$.code").value("C001"));
    }

    @Test
    @DisplayName("회원가입 - 이름 길이 불일치 (2글자 미만)")
    void signupTest5() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        post("/api/v1/signup")
                                .content("""
                                         {
                                             "email": "test@test.com",
                                             "password": "test1234!",
                                             "name": "t",
                                             "address": "address",
                                             "detailAddress": "detailAddress",
                                             "phone": "01012345678"
                                         }
                                         """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("signup"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값"))
                .andExpect(jsonPath("$.code").value("C001"));
    }

    @Test
    @DisplayName("회원가입 - 전화번호 형식 불일치")
    void signupTest6() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        post("/api/v1/signup")
                                .content("""
                                         {
                                             "email": "test@test.com",
                                             "password": "test1234!",
                                             "name": "test1",
                                             "address": "address",
                                             "detailAddress": "detailAddress",
                                             "phone": "010-1234-5678"
                                         }
                                         """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("signup"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값"))
                .andExpect(jsonPath("$.code").value("C001"));
    }

    @Test
    @DisplayName("회원가입 - 필수 값 누락 (주소)")
    void signupTest7() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        post("/api/v1/signup")
                                .content("""
                                         {
                                             "email": "test@test.com",
                                             "password": "test1234!",
                                             "name": "test1",
                                             "detailAddress": "detailAddress",
                                             "phone": "01012345678"
                                         }
                                         """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("signup"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값"))
                .andExpect(jsonPath("$.code").value("C001"));
    }

    @Test
    @DisplayName("회원가입 - 필수 값 누락 (상세주소)")
    void signupTest8() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        post("/api/v1/signup")
                                .content("""
                                         {
                                             "email": "test@test.com",
                                             "password": "test1234!",
                                             "name": "test1",
                                             "address": "detailAddress",
                                             "phone": "01012345678"
                                         }
                                         """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("signup"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값"))
                .andExpect(jsonPath("$.code").value("C001"));
    }

    @Test
    @DisplayName("회원가입 - 이메일 중복")
    void signupTest9() throws Exception {
        // 첫 번째 회원가입
        mockMvc.perform(
                post("/api/v1/signup")
                        .content("""
                                 {
                                     "email": "test@test.com",
                                     "password": "test1234!",
                                     "name": "test1",
                                     "address": "address",
                                     "detailAddress": "detailAddress",
                                     "phone": "01012345678"
                                 }
                                 """.stripIndent())
                        .contentType(
                                new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                        )
        );

        ResultActions resultActions = mockMvc
                .perform(
                        post("/api/v1/signup")
                                .content("""
                                         {
                                             "email": "test@test.com",
                                             "password": "test5678!",
                                             "name": "test2",
                                             "address": "address2",
                                             "detailAddress": "detailAddress2",
                                             "phone": "01087654321"
                                         }
                                         """.stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("signup"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("이미 사용중인 이메일"))
                .andExpect(jsonPath("$.code").value("U001"));
    }

    @Test
    @DisplayName("회원정보")
    void profileTest1() throws Exception {
        // 회원가입
        mockMvc.perform(
                post("/api/v1/signup")
                        .content("""
                                 {
                                     "email": "test123@test.com",
                                     "password": "test1234!",
                                     "name": "test1",
                                     "address": "address",
                                     "detailAddress": "detailAddress",
                                     "phone": "01012345678"
                                 }
                                 """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andExpect(status().isCreated());

        // 방금 생성된 유저 찾기
        User user = userRepository.findByEmail("test123@test.com").orElseThrow();

        ResultActions loginResult = mockMvc.perform(
                post("/api/v1/login")
                        .content("""
                                {
                                    "email": "test123@test.com",
                                    "password": "test1234!"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String accessToken = loginResult
                .andReturn()
                        .getResponse()
                                .getHeader("Authorization");

        // 회원정보 조회
        mockMvc.perform(
                       get("/api/v1/users/" + user.getId())
                               .header("Authorization", accessToken)
                               .contentType(MediaType.APPLICATION_JSON)
               )
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true))
               .andExpect(jsonPath("$.message").value("회원 정보를 성공적으로 불러왔습니다."))
               .andExpect(jsonPath("$.code").value("200"))
               .andExpect(jsonPath("$.data.name").value("test1"))
               .andExpect(jsonPath("$.data.email").value("test123@test.com"))
               .andExpect(jsonPath("$.data.status").value("ACTIVATED"))
               .andExpect(jsonPath("$.data.role").value("ROLE_USER"))
               .andExpect(jsonPath("$.data.phone").value("01012345678"))
               .andExpect(jsonPath("$.data.address").value("address"))
               .andExpect(jsonPath("$.data.detailAddress").value("detailAddress"))
               .andExpect(jsonPath("$.data.created_date").exists())
               .andExpect(jsonPath("$.data.modified_date").exists());
    }

    @Test
    @CustomWithMockAdmin
    @DisplayName("회원정보 - 존재하지 않는 회원")
    void profileTest2() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("getUser"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("회원 정보가 존재하지 않음"))
                .andExpect(jsonPath("$.code").value("U002"));
    }

    @Test
    @DisplayName("회원정보 조회 - 탈퇴한 회원")
    void profileTest3() throws Exception {
        // 회원가입
        mockMvc.perform(
                post("/api/v1/signup")
                        .content("""
                                 {
                                     "email": "test123@test.com",
                                     "password": "test1234!",
                                     "name": "test1",
                                     "address": "address",
                                     "detailAddress": "detailAddress",
                                     "phone": "01012345678"
                                 }
                                 """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andExpect(status().isCreated());

        User user = userRepository.findByEmail("test123@test.com").orElseThrow();

        ResultActions loginResult = mockMvc.perform(
                post("/api/v1/login")
                        .content("""
                                {
                                    "email": "test123@test.com",
                                    "password": "test1234!"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String accessToken = loginResult
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        // 회원 탈퇴
        mockMvc.perform(
                delete("/api/v1/users/" + user.getId())
                        .header("Authorization", accessToken)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andExpect(status().isOk());

        // 탈퇴한 회원 정보 조회 시도
        ResultActions resultActions = mockMvc
                .perform(
                        get("/api/v1/users/" + user.getId())
                                .header("Authorization", accessToken)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("getUser"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("U005"))
                .andExpect(jsonPath("$.message").value("탈퇴한 회원"));
    }

    @Test
    @DisplayName("회원정보 수정")
    void modifyUserTest1() throws Exception {
        // 회원가입
        mockMvc.perform(
                post("/api/v1/signup")
                        .content("""
                                 {
                                     "email": "test123@test.com",
                                     "password": "test1234!",
                                     "name": "test1",
                                     "address": "address",
                                     "detailAddress": "detailAddress",
                                     "phone": "01012345678"
                                 }
                                 """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        );

        User user = userRepository.findByEmail("test123@test.com").orElseThrow();

        ResultActions loginResult = mockMvc.perform(
                post("/api/v1/login")
                        .content("""
                                {
                                    "email": "test123@test.com",
                                    "password": "test1234!"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String accessToken = loginResult
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        // 회원정보 수정
        ResultActions resultActions = mockMvc
                .perform(
                        patch("/api/v1/users/" + user.getId())
                                .header("Authorization", accessToken)
                                .content("""
                                         {
                                             "name": "modified",
                                             "address": "modified address",
                                             "detailAddress": "modified detailAddress",
                                             "phone": "01087654321"
                                         }
                                         """.stripIndent())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("modifyUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value("회원 정보를 성공적으로 수정하였습니다."))
                .andExpect(jsonPath("$.code").value("200"));

        // 수정된 정보 확인
        mockMvc.perform(
                       get("/api/v1/users/" + user.getId())
                               .header("Authorization", accessToken)
                               .contentType(MediaType.APPLICATION_JSON)
               )
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data.name").value("modified"))
               .andExpect(jsonPath("$.data.address").value("modified address"))
               .andExpect(jsonPath("$.data.detailAddress").value("modified detailAddress"))
               .andExpect(jsonPath("$.data.phone").value("01087654321"));
    }

    @Test
    @CustomWithMockAdmin
    @DisplayName("회원정보 수정 - 존재하지 않는 회원")
    void modifyUserTest2() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        patch("/api/v1/users/3000")
                                .content("""
                                         {
                                             "name": "modified",
                                             "address": "modified address",
                                             "detailAddress": "modified detailAddress",
                                             "phone": "01087654321"
                                         }
                                         """.stripIndent())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("modifyUser"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("회원 정보가 존재하지 않음"))
                .andExpect(jsonPath("$.code").value("U002"));
    }

    @Test
    @DisplayName("회원정보 수정 - 잘못된 전화번호 형식")
    void modifyUserTest3() throws Exception {
        // 회원가입
        mockMvc.perform(
                post("/api/v1/signup")
                        .content("""
                                 {
                                     "email": "test123@test.com",
                                     "password": "test1234!",
                                     "name": "test1",
                                     "address": "address",
                                     "detailAddress": "modified detailAddress",
                                     "phone": "01012345678"
                                 }
                                 """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        );

        User user = userRepository.findByEmail("test123@test.com").orElseThrow();

        ResultActions loginResult = mockMvc.perform(
                post("/api/v1/login")
                        .content("""
                                {
                                    "email": "test123@test.com",
                                    "password": "test1234!"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String accessToken = loginResult
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        // 잘못된 형식으로 수정 시도
        ResultActions resultActions = mockMvc
                .perform(
                        patch("/api/v1/users/" + user.getId())
                                .header("Authorization", accessToken)
                                .content("""
                                         {
                                             "name": "modified",
                                             "address": "modified address",
                                             "detailAddress": "modified detailAddress",
                                             "phone": "010-1234-5678"
                                         }
                                         """.stripIndent())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("modifyUser"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값"))
                .andExpect(jsonPath("$.code").value("C001"));
    }

    @Test
    @DisplayName("회원정보 수정 - 탈퇴한 회원")
    void modifyUserTest4() throws Exception {
        // 회원가입
        mockMvc.perform(
                post("/api/v1/signup")
                        .content("""
                                 {
                                     "email": "test123@test.com",
                                     "password": "test1234!",
                                     "name": "test1",
                                     "address": "address",
                                     "detailAddress": "detailAddress",
                                     "phone": "01012345678"
                                 }
                                 """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andExpect(status().isCreated());

        User user = userRepository.findByEmail("test123@test.com").orElseThrow();

        ResultActions loginResult = mockMvc.perform(
                post("/api/v1/login")
                        .content("""
                                {
                                    "email": "test123@test.com",
                                    "password": "test1234!"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String accessToken = loginResult
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        // 회원 탈퇴
        mockMvc.perform(
                delete("/api/v1/users/" + user.getId())
                        .header("Authorization", accessToken)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andExpect(status().isOk());

        // 탈퇴한 회원 정보 수정 시도
        ResultActions resultActions = mockMvc
                .perform(
                        patch("/api/v1/users/" + user.getId())
                                .header("Authorization", accessToken)
                                .content("""
                                         {
                                             "name": "modified",
                                             "address": "modified address",
                                             "detailAddress": "modified detailAddress",
                                             "phone": "01087654321"
                                         }
                                         """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("modifyUser"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("U005"))
                .andExpect(jsonPath("$.message").value("탈퇴한 회원"));
    }

    @Test
    @DisplayName("비밀번호 찾기")
    void changePasswordTest1() throws Exception {
        // 회원가입
        mockMvc.perform(
                post("/api/v1/signup")
                        .content("""
                                 {
                                     "email": "test123@test.com",
                                     "password": "test1234!",
                                     "name": "test1",
                                     "address": "address",
                                     "detailAddress": "detailAddress",
                                     "phone": "01012345678"
                                 }
                                 """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andExpect(status().isCreated());

        // 방금 생성된 유저 찾기
        User user = userRepository.findByEmail("test123@test.com").orElseThrow();

        ResultActions loginResult = mockMvc.perform(
                post("/api/v1/login")
                        .content("""
                                {
                                    "email": "test123@test.com",
                                    "password": "test1234!"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String accessToken = loginResult
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        // 비밀번호 변경
        ResultActions resultActions = mockMvc
                .perform(
                        patch("/api/v1/users/" + user.getId() + "/password")
                                .header("Authorization", accessToken)
                                .content("""
                                         {
                                             "email": "test123@test.com",
                                             "newPassword": "modifyPassword1234!"
                                         }
                                         """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                ).andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("changePassword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("비밀번호를 성공적으로 변경하였습니다."));
    }

    @Test
    @DisplayName("비밀번호 변경 - 이메일 불일치")
    void changePasswordTest2() throws Exception {
        // 회원가입
        mockMvc.perform(
                post("/api/v1/signup")
                        .content("""
                                 {
                                     "email": "test123@test.com",
                                     "password": "test1234!",
                                     "name": "test1",
                                     "address": "address",
                                     "detailAddress": "detailAddress",
                                     "phone": "01012345678"
                                 }
                                 """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andExpect(status().isCreated());

        User user = userRepository.findByEmail("test123@test.com").orElseThrow();

        ResultActions loginResult = mockMvc.perform(
                post("/api/v1/login")
                        .content("""
                                {
                                    "email": "test123@test.com",
                                    "password": "test1234!"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String accessToken = loginResult
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        // 다른 이메일로 비밀번호 변경 시도
        ResultActions resultActions = mockMvc
                .perform(
                        patch("/api/v1/users/" + user.getId() + "/password")
                                .header("Authorization", accessToken)
                                .content("""
                                         {
                                             "email": "wrong@test.com",
                                             "newPassword": "modifyPassword1234!"
                                         }
                                         """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("changePassword"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("C001"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값"));
    }

    @Test
    @DisplayName("비밀번호 변경 - 새 비밀번호 형식 불일치")
    void changePasswordTest3() throws Exception {
        // 회원가입
        mockMvc.perform(
                post("/api/v1/signup")
                        .content("""
                                 {
                                     "email": "test123@test.com",
                                     "password": "test1234!",
                                     "name": "test1",
                                     "address": "address",
                                     "detailAddress": "detailAddress",
                                     "phone": "01012345678"
                                 }
                                 """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andExpect(status().isCreated());

        User user = userRepository.findByEmail("test123@test.com").orElseThrow();

        ResultActions loginResult = mockMvc.perform(
                post("/api/v1/login")
                        .content("""
                                {
                                    "email": "test123@test.com",
                                    "password": "test1234!"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String accessToken = loginResult
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        // 잘못된 형식의 비밀번호로 변경 시도
        ResultActions resultActions = mockMvc
                .perform(
                        patch("/api/v1/users/" + user.getId() + "/password")
                                .header("Authorization", accessToken)
                                .content("""
                                         {
                                             "email": "test123@test.com",
                                             "newPassword": "short"
                                         }
                                         """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("changePassword"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("C001"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력값"));
    }

    @Test
    @DisplayName("비밀번호 변경 - 현재 비밀번호와 동일")
    void changePasswordTest4() throws Exception {
        // 회원가입
        mockMvc.perform(
                post("/api/v1/signup")
                        .content("""
                                 {
                                     "email": "test123@test.com",
                                     "password": "test1234!",
                                     "name": "test1",
                                     "address": "address",
                                     "detailAddress": "detailAddress",
                                     "phone": "01012345678"
                                 }
                                 """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andExpect(status().isCreated());

        User user = userRepository.findByEmail("test123@test.com").orElseThrow();

        ResultActions loginResult = mockMvc.perform(
                post("/api/v1/login")
                        .content("""
                                {
                                    "email": "test123@test.com",
                                    "password": "test1234!"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String accessToken = loginResult
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        // 현재 비밀번호와 동일한 비밀번호로 변경 시도
        ResultActions resultActions = mockMvc
                .perform(
                        patch("/api/v1/users/" + user.getId() + "/password")
                                .header("Authorization", accessToken)
                                .content("""
                                         {
                                             "email": "test123@test.com",
                                             "newPassword": "test1234!"
                                         }
                                         """.stripIndent())
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("changePassword"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("U004"))
                .andExpect(jsonPath("$.message").value("새 비밀번호가 현재 비밀번호와 동일"));
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공")
    void changePasswordTest5() throws Exception {
        // 회원가입
        mockMvc.perform(
                post("/api/v1/signup")
                        .content("""
                                 {
                                     "email": "test123@test.com",
                                     "password": "test1234!",
                                     "name": "test1",
                                     "address": "address",
                                     "detailAddress": "detailAddress",
                                     "phone": "01012345678"
                                 }
                                 """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andExpect(status().isCreated());

        User user = userRepository.findByEmail("test123@test.com").orElseThrow();

        ResultActions loginResult = mockMvc.perform(
                post("/api/v1/login")
                        .content("""
                                {
                                    "email": "test123@test.com",
                                    "password": "test1234!"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String accessToken = loginResult
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        // 비밀번호 변경 요청
        mockMvc.perform(
                patch("/api/v1/users/" + user.getId() + "/password")
                        .header("Authorization", accessToken)
                        .content("""
                                 {
                                     "email": "test123@test.com",
                                     "newPassword": "modifyPassword1234!"
                                 }
                                 """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    @DisplayName("회원 탈퇴")
    void deleteUserTest1() throws Exception {
        // 회원가입
        mockMvc.perform(
                post("/api/v1/signup")
                        .content("""
                                 {
                                     "email": "test123@test.com",
                                     "password": "test1234!",
                                     "name": "test1",
                                     "address": "address",
                                     "detailAddress": "detailAddress",
                                     "phone": "01012345678"
                                 }
                                 """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andExpect(status().isCreated());

        User user = userRepository.findByEmail("test123@test.com").orElseThrow();

        ResultActions loginResult = mockMvc.perform(
                post("/api/v1/login")
                        .content("""
                                {
                                    "email": "test123@test.com",
                                    "password": "test1234!"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String accessToken = loginResult
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        // 회원 탈퇴
        ResultActions resultActions = mockMvc
                .perform(
                        delete("/api/v1/users/" + user.getId())
                                .header("Authorization", accessToken)
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("deleteUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("탈퇴가 성공적으로 이루어졌습니다."));

        // DB에서 사용자 다시 조회하여 상태가 DELETED로 변경되었는지 확인
        User deletedUser = userRepository.findByEmail("test123@test.com").orElseThrow();
        assertEquals(UserStatus.DELETED.toString(), deletedUser.getStatus());
    }

    @Test
    @DisplayName("회원 탈퇴 - 이미 탈퇴한 회원")
    void deleteUserTest2() throws Exception {
        // 회원가입
        mockMvc.perform(
                post("/api/v1/signup")
                        .content("""
                                 {
                                     "email": "test123@test.com",
                                     "password": "test1234!",
                                     "name": "test1",
                                     "address": "address",
                                     "detailAddress": "detailAddress",
                                     "phone": "01012345678"
                                 }
                                 """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andExpect(status().isCreated());

        User user = userRepository.findByEmail("test123@test.com").orElseThrow();

        ResultActions loginResult = mockMvc.perform(
                post("/api/v1/login")
                        .content("""
                                {
                                    "email": "test123@test.com",
                                    "password": "test1234!"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String accessToken = loginResult
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        // 첫 번째 회원 탈퇴
        mockMvc.perform(
                delete("/api/v1/users/" + user.getId())
                        .header("Authorization", accessToken)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andExpect(status().isOk());

        // 두 번째 회원 탈퇴 시도
        ResultActions resultActions = mockMvc
                .perform(
                        delete("/api/v1/users/" + user.getId())
                                .header("Authorization", accessToken)
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1UserController.class))
                .andExpect(handler().methodName("deleteUser"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("U005"))
                .andExpect(jsonPath("$.message").value("탈퇴한 회원"));
    }

    @Test
    @DisplayName("회원 주문 목록 조회")
    void getOrdersByUser() throws Exception {
//        User customer = User.builder()
//                            .email("test123@test.com")
//                            .password("test1234!")
//                            .name("user")
//                            .address("user address")
//                            .detailAddress("user detail address")
//                            .phone("01000000000")
//                            .status("ACTIVATE")
//                            .role("ROLE_USER")
//                            .build();
        mockMvc.perform(
                post("/api/v1/signup")
                        .content("""
                                 {
                                     "email": "test123@test.com",
                                     "password": "test1234!",
                                     "name": "test1",
                                     "address": "address",
                                     "detailAddress": "detailAddress",
                                     "phone": "01012345678"
                                 }
                                 """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andExpect(status().isCreated());

        User customer = userRepository.findByEmail("test123@test.com").orElseThrow();

        ResultActions loginResult = mockMvc.perform(
                post("/api/v1/login")
                        .content("""
                                {
                                    "email": "test123@test.com",
                                    "password": "test1234!"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String accessToken = loginResult
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        Order order = Order.of(customer, "orderNumber", 1, BigDecimal.valueOf(10000.00),
                               "%s %s".formatted(customer.getAddress(), customer.getDetailAddress()));
        ReflectionUtil.setPrivateFieldValue(Order.class, order, "createdDate", LocalDateTime.now());

        OrderResponse orderResponse = OrderResponse.of(order);

        when(orderService.getOrdersByUserId(anyLong())).thenReturn(List.of(orderResponse));

        //When
        ResultActions resultActions = mockMvc.perform(get("/api/v1/users/orders")
                        .header("Authorization", accessToken)
                                                              .contentType(new MediaType(MediaType.APPLICATION_JSON,
                                                                                         StandardCharsets.UTF_8)))
                                             .andDo(print());

        //Then
        resultActions.andExpect(handler().handlerType(ApiV1UserController.class))
                     .andExpect(handler().methodName("getOrdersByUser"))
                     .andExpect(status().isOk())
                     .andExpect(jsonPath("$.isSuccess").value(true))
                     .andExpect(jsonPath("$.code").value("200"))
                     .andExpect(jsonPath("$.message").value("회원의 주문 정보를 성공적으로 조회했습니다."))
                     .andExpect(jsonPath("$.data").exists())
                     .andExpect(jsonPath("$.data[0].orderNumber").value(order.getOrderNumber()))
                     .andExpect(jsonPath("$.data[0].name").value(customer.getName()))
                     .andExpect(jsonPath("$.data[0].totalAmount").value(order.getTotalAmount()))
                     .andExpect(jsonPath("$.data[0].totalPrice").value(order.getTotalPrice()))
                     .andExpect(jsonPath("$.data[0].orderAddress").value(order.getAddress()))
                     .andExpect(jsonPath("$.data[0].orderStatus").value(order.getStatus().name()))
                     .andExpect(jsonPath("$.data[0].orderList").isEmpty())
                     .andExpect(jsonPath("$.data[0].createdDate")
                                        .value(Ut.Str.localDateTimeToString(order.getCreatedDate())));
    }

}
