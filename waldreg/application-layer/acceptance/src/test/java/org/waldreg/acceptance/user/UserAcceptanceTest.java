package org.waldreg.acceptance.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class UserAcceptanceTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String apiVersion = "1.0";

    @Test
    @DisplayName("유저 생성 성공 테스트")
    public void CREATE_NEW_USER_SUCCESS_TEST() throws Exception{
        //given
        String url = "/user";
        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd";
        String phoneNumber = "010-1234-1234";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        //when
        ResultActions result = UserAcceptanceTestHelper.createUser(mvc, url,
                objectMapper.writeValueAsString(userCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 생성 실패 인수 테스트 - 중복 아이디")
    public void CREATE_NEW_USER_FAIL_CAUSE_DUPLICATE_USER_ID() throws Exception{
        //given
        String url = "/user";
        String name1 = "alcuk1";
        String userId1 = "alcuk_id";
        String userPassword1 = "alcuk_pwd1";
        String phoneNumber1 = "010-1234-1111";
        UserCreateRequest userCreateRequest1 = UserCreateRequest.builder()
                .name(name1)
                .userId(userId1)
                .userPassword(userPassword1)
                .phoneNumber(phoneNumber1)
                .build();

        String name2 = "alcuk2";
        String userId2 = "alcuk_id";
        String userPassword2 = "alcuk_pwd2";
        String phoneNumber2 = "010-1234-2222";
        UserCreateRequest userCreateRequest2 = UserCreateRequest.builder()
                .name(name2)
                .userId(userId2)
                .userPassword(userPassword2)
                .phoneNumber(phoneNumber2)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, url,
                objectMapper.writeValueAsString(userCreateRequest1));

        ResultActions result = UserAcceptanceTestHelper.createUser(mvc, url,
                objectMapper.writeValueAsString(userCreateRequest2));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Duplicated user_id"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 생성 실패 인수 테스트 - 비밀번호 보안 기준 미달")
    public void CREATE_NEW_USER_FAIL_CAUSE_UNSECURE_USER_PWD() throws Exception{
        //given
        String url = "/user";
        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "";
        String phoneNumber = "010-1234-1234";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        //when
        ResultActions result = UserAcceptanceTestHelper.createUser(mvc, url,
                objectMapper.writeValueAsString(userCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unsecured user_password"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 생성 실패 인수 테스트 - 잘못된 입력")
    public void CREATE_NEW_USER_FAIL_CAUSE_INVALID_REQUEST() throws Exception{
        //given
        String url = "/user";
        String name = "";
        String userId = "";
        String userPassword = "";
        String phoneNumber = "";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        //when
        ResultActions result = UserAcceptanceTestHelper.createUser(mvc, url,
                objectMapper.writeValueAsString(userCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid request"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("특정 유저 조회 성공 테스트 - 토큰 있을 때")
    public void INQUIRY_USER_WITH_TOKEN_SUCCESS_TEST() throws Exception{
        //given
        String saveUrl = "/user";
        String readUrl = "/user/{name}";
        String token = "mock_token";

        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, saveUrl,
                objectMapper.writeValueAsString(userCreateRequest));
        ResultActions result = UserAcceptanceTestHelper.inquiryUserWithToken(mvc, readUrl,
                userCreateRequest.getName(), token);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.name").value(userCreateRequest.getName()),
                MockMvcResultMatchers.jsonPath("$.user_id").value(userCreateRequest.getUserId()),
                MockMvcResultMatchers.jsonPath("$.phone_number")
                        .value(userCreateRequest.getPhoneNumber()),
                MockMvcResultMatchers.jsonPath("$.character").isString(),
                MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.advantage").isNumber(),
                MockMvcResultMatchers.jsonPath("$.penalty").isNumber(),
                MockMvcResultMatchers.jsonPath("$.social_login").isArray()
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("특정 유저 조회 성공 테스트 - 토큰 없을 때")
    public void INQUIRY_USER_WITHOUT_TOKEN_SUCCESS_TEST() throws Exception{
        //given
        String saveUrl = "/user";
        String readUrl = "/user/{name}";

        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";

        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, saveUrl,
                objectMapper.writeValueAsString(userCreateRequest));
        ResultActions result = UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, readUrl,
                userCreateRequest.getName());

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.name").value(userCreateRequest.getName()),
                MockMvcResultMatchers.jsonPath("$.user_id").value(userCreateRequest.getUserId()),
                MockMvcResultMatchers.jsonPath("$.character").isString(),
                MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty()
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("특정 유저 조회 실패 테스트 - 없는 유저")
    public void INQUIRY_USER_FAIL_CAUSE_UNKNOWN_USER_TEST() throws Exception{
        //given
        String saveUrl = "/user";
        String readUrl = "/user/{name}";
        String unknownUser = "unknown";

        //when
        ResultActions result = UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, readUrl,
                unknownUser);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown user name"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("로그인된 유저 조회 성공 테스트")
    public void INQUIRY_USER_ONLINE_SUCCESS_TEST() throws Exception{
        //given
        String url = "/user";
        String token = "mock_token";

        //when
        ResultActions result = UserAcceptanceTestHelper.inquiryUserOnline(mvc, url, token);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.name").isString(),
                MockMvcResultMatchers.jsonPath("$.user_id").isString(),
                MockMvcResultMatchers.jsonPath("$.phone_number").isString(),
                MockMvcResultMatchers.jsonPath("$.character").isString(),
                MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.advantage").isNumber(),
                MockMvcResultMatchers.jsonPath("$.penalty").isNumber(),
                MockMvcResultMatchers.jsonPath("$.social_login").isArray()
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("로그인된 유저 조회 실패 테스트 - 잘못된 토큰")
    public void INQUIRY_USER_ONLINE_FAIL_CAUSE_INVALID_TOKEN_TEST() throws Exception{
        //given
        String url = "/user";
        String token = "mock_token";
        String invalidToken = "";

        //when
        ResultActions result = UserAcceptanceTestHelper.inquiryUserOnline(mvc, url, invalidToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid token"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("로그인된 유저 조회 실패 테스트 - 없는 토큰")
    public void INQUIRY_USER_ONLINE_FAIL_CAUSE_EMPTY_TOKEN_TEST() throws Exception{
        //given
        String url = "/user";

        //when
        ResultActions result = UserAcceptanceTestHelper.inquiryUserOnlineWithNoToken(mvc, url);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Empty token"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 수정 성공 테스트 - 3가지 수정사항")
    public void MODIFY_USER_3_VALUE_SUCCESS_TEST() throws Exception{
        //given
        String url = "/user";
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String token = "mock_token";
        String password = userCreateRequest.getUserPassword();
        String modifiedName = "alcuk2";
        String modifiedUserPassword = "alcuk_pwd2";
        String modifiedPhoneNumber = "010-1234-1234";
        UserCreateRequest modifyUserCreateRequest = UserCreateRequest.builder()
                .name(modifiedName)
                .userPassword(modifiedUserPassword)
                .phoneNumber(modifiedPhoneNumber)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, url,
                objectMapper.writeValueAsString(userCreateRequest));
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithToken(mvc, url, token,
                password,
                objectMapper.writeValueAsString(modifyUserCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 수정 성공 테스트 - 1가지 수정사항")
    public void MODIFY_USER_1_VALUE_SUCCESS_TEST() throws Exception{
        //given
        String url = "/user";
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String token = "mock_token";
        String password = userCreateRequest.getUserPassword();
        String modifiedName = "alcuk2";
        UserCreateRequest modifyUserCreateRequest = UserCreateRequest.builder()
                .name(modifiedName)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, url,
                objectMapper.writeValueAsString(userCreateRequest));
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithToken(mvc, url, token,
                password,
                objectMapper.writeValueAsString(modifyUserCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 수정 성공 테스트 - 2가지 수정사항")
    public void MODIFY_USER_2_VALUE_SUCCESS_TEST() throws Exception{
        //given
        String url = "/user";
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String token = "mock_token";
        String password = userCreateRequest.getUserPassword();
        String modifiedName = "alcuk2";
        String modifiedPhoneNumber = "010-1234-1234";
        UserCreateRequest modifyUserCreateRequest = UserCreateRequest.builder()
                .name(modifiedName)
                .phoneNumber(modifiedPhoneNumber)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, url,
                objectMapper.writeValueAsString(userCreateRequest));
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithToken(mvc, url, token,
                password,
                objectMapper.writeValueAsString(modifyUserCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 수정 실패 테스트 - 헤더에 있는 비밀번호가 저장된 비밀번호 다름")
    public void MODIFY_USER_FAIL_CAUSE_WRONG_PASSWORD_TEST() throws Exception{
        //given
        String url = "/user";
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String token = "mock_token";
        String invalidPassword = "";
        String modifiedName = "alcuk2";
        UserCreateRequest modifyUserCreateRequest = UserCreateRequest.builder()
                .name(modifiedName)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, url,
                objectMapper.writeValueAsString(userCreateRequest));
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithToken(mvc, url, token,
                invalidPassword,
                objectMapper.writeValueAsString(modifyUserCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid password"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 수정 실패 테스트 - 보안 기준 미달 비밀번호로 수정 시도")
    public void MODIFY_USER_FAIL_CAUSE_UNSECURED_PASSWORD_TEST() throws Exception{
        //given
        String url = "/user";
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String token = "mock_token";
        String password = userCreateRequest.getUserPassword();
        String modifiedUserPassword = "";
        UserCreateRequest modifyUserCreateRequest = UserCreateRequest.builder()
                .userPassword(modifiedUserPassword)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, url,
                objectMapper.writeValueAsString(userCreateRequest));
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithToken(mvc, url, token,
                password,
                objectMapper.writeValueAsString(modifyUserCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unsecured user_password"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 수정 실패 테스트 - 잘못된 토큰")
    public void MODIFY_USER_1_VALUE_FAIL_INVALID_TOKEN_TEST() throws Exception{
        //given
        String url = "/user";
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String invalidToken = "";
        String password = userCreateRequest.getUserPassword();
        String modifiedName = "alcuk2";
        UserCreateRequest modifyUserCreateRequest = UserCreateRequest.builder()
                .name(modifiedName)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, url,
                objectMapper.writeValueAsString(userCreateRequest));
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithToken(mvc, url, invalidToken,
                password,
                objectMapper.writeValueAsString(modifyUserCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid token"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 수정 실패 테스트 - 없는 토큰")
    public void MODIFY_USER_1_VALUE_FAIL_EMPTY_TOKEN_TEST() throws Exception{
        //given
        String url = "/user";
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String password = userCreateRequest.getUserPassword();
        String modifiedName = "alcuk2";
        UserCreateRequest modifyUserCreateRequest = UserCreateRequest.builder()
                .name(modifiedName)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, url,
                objectMapper.writeValueAsString(userCreateRequest));
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithoutToken(mvc, url, password,
                objectMapper.writeValueAsString(modifyUserCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Empty token"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 보안 삭제 성공 테스트")
    public void SECURED_DELETE_USER_SUCCESS_TEST() throws Exception{
        //given
        String url = "/user";
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String token = "mock_token";
        String password = userCreateRequest.getUserPassword();

        //when
        UserAcceptanceTestHelper.createUser(mvc, url,
                objectMapper.writeValueAsString(userCreateRequest));
        ResultActions result = UserAcceptanceTestHelper.deleteUserWithToken(mvc, url, token,
                password);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 보안 삭제 삭제 테스트 - 잘못된 비밀번호")
    public void SECURED_DELETE_USER_FAIL_CAUSE_INVALID_PASSWORD_TEST() throws Exception{
        //given
        String url = "/user";
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String token = "mock_token";
        String invalidPassword = "";

        //when
        UserAcceptanceTestHelper.createUser(mvc, url,
                objectMapper.writeValueAsString(userCreateRequest));
        ResultActions result = UserAcceptanceTestHelper.deleteUserWithToken(mvc, url, token,
                invalidPassword);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid password"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 보안 삭제 삭제 테스트 - 잘못된 토큰")
    public void SECURED_DELETE_USER_FAIL_CAUSE_INVALID_TOKEN_TEST() throws Exception{
        //given
        String url = "/user";
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String invalidToken = "";
        String password = userCreateRequest.getUserPassword();

        //when
        UserAcceptanceTestHelper.createUser(mvc, url,
                objectMapper.writeValueAsString(userCreateRequest));
        ResultActions result = UserAcceptanceTestHelper.deleteUserWithToken(mvc, url, invalidToken,
                password);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid token"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("유저 보안 삭제 삭제 테스트 - 없는 토큰")
    public void SECURED_DELETE_USER_FAIL_CAUSE_EMPTY_TOKEN_TEST() throws Exception{
        //given
        String url = "/user";
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String password = userCreateRequest.getUserPassword();

        //when
        UserAcceptanceTestHelper.createUser(mvc, url,
                objectMapper.writeValueAsString(userCreateRequest));
        ResultActions result = UserAcceptanceTestHelper.deleteUserWithoutToken(mvc, url, password);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Empty token"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    private final static class UserCreateRequest{

        @JsonProperty("name")
        private String name;
        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("user_password")
        private String userPassword;
        @JsonProperty("phone_number")
        private String phoneNumber;

        public UserCreateRequest(){}

        private UserCreateRequest(Builder builder){
            this.name = builder.name;
            this.userId = builder.userId;
            this.userPassword = builder.userPassword;
            this.phoneNumber = builder.phoneNumber;
        }

        public static Builder builder(){return new Builder();}

        public String getName(){return name;}

        public String getUserId(){return userId;}

        public String getUserPassword(){return userPassword;}

        public String getPhoneNumber(){return phoneNumber;}

        public void setName(String name){this.name = name;}

        public void setUserId(String userId){this.userId = userId;}

        public void setUserPassword(String userPassword){this.userPassword = userPassword;}

        public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}

        public final static class Builder{

            private String name;
            private String userId;
            private String userPassword;
            private String phoneNumber;

            private Builder(){}

            public Builder name(String name){
                this.name = name;
                return this;
            }

            public Builder userId(String userId){
                this.userId = userId;
                return this;
            }

            public Builder userPassword(String userPassword){
                this.userPassword = userPassword;
                return this;
            }

            public Builder phoneNumber(String phoneNumber){
                this.phoneNumber = phoneNumber;
                return this;
            }

            public UserCreateRequest build(){return new UserCreateRequest(this);}

        }

    }

}
