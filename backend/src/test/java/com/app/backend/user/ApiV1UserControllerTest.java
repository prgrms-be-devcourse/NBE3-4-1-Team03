package com.app.backend.user;

import com.app.backend.domain.user.controller.ApiV1UserController;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ApiV1UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

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

        // 회원정보 조회
        mockMvc.perform(
                get("/api/v1/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value("회원 정보를 성공적으로 불러왔습니다."))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[1].name").value("test1"))
                .andExpect(jsonPath("$.data[1].email").value("test123@test.com"))
                .andExpect(jsonPath("$.data[1].status").value("ACTIVATED"))
                .andExpect(jsonPath("$.data[1].role").value("ROLE_USER"))
                .andExpect(jsonPath("$.data[1].phone").value("01012345678"))
                .andExpect(jsonPath("$.data[1].address").value("address"))
                .andExpect(jsonPath("$.data[1].detailAddress").value("detailAddress"))
                .andExpect(jsonPath("$.data[1].created_date").exists())
                .andExpect(jsonPath("$.data[1].modified_date").exists());
    }

    @Test
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
    @DisplayName("회원정보 수정")
    void modifyUserTest1() throws Exception {
        // 회원가입
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
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        );

        // 회원정보 수정
        ResultActions resultActions = mockMvc
                .perform(
                        patch("/api/v1/users/1")
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
                        get("/api/v1/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[1].name").value("modified"))
                .andExpect(jsonPath("$.data[1].address").value("modified address"))
                .andExpect(jsonPath("$.data[1].detailAddress").value("modified detailAddress"))
                .andExpect(jsonPath("$.data[1].phone").value("01087654321"));
    }

    @Test
    @DisplayName("회원정보 수정 - 존재하지 않는 회원")
    void modifyUserTest2() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        patch("/api/v1/users/3")
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
                                    "email": "test@test.com",
                                    "password": "test1234!",
                                    "name": "test1",
                                    "address": "address",
                                    "detailAddress": "modified detailAddress",
                                    "phone": "01012345678"
                                }
                                """.stripIndent())
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        );

        // 잘못된 형식으로 수정 시도
        ResultActions resultActions = mockMvc
                .perform(
                        patch("/api/v1/users/1")
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

}
