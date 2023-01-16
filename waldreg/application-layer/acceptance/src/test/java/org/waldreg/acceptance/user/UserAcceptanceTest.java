package org.waldreg.acceptance.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.waldreg.acceptance.authentication.AuthenticationAcceptanceTestHelper;
import org.waldreg.controller.user.request.UserRequest;
import org.waldreg.controller.user.response.UserResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class UserAcceptanceTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String apiVersion = "1.0";

    private ArrayList<UserRequest> userCreateRequestList;

    {
        userCreateRequestList = new ArrayList<>();
    }

    @BeforeEach
    @AfterEach
    public void INITIATE() throws Exception{
        String url = "/user/{id}";
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        for (UserRequest request : userCreateRequestList){
            UserResponse userResponse = objectMapper.readValue(UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, request.getUserId())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(), UserResponse.class);
            UserAcceptanceTestHelper.forcedDeleteUserWithToken(mvc, userResponse.getId(), adminToken);
            ResultActions result = UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, request.getUserId());
            result.andExpectAll(
                    MockMvcResultMatchers.status().isBadRequest(),
                    MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                    MockMvcResultMatchers.header().string("api-version", apiVersion),
                    MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                    MockMvcResultMatchers.jsonPath("$.messages").value("Unknown user id"),
                    MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
            ).andDo(MockMvcResultHandlers.print());
        }
        userCreateRequestList.clear();
    }

    @Test
    @DisplayName("유저 생성 성공 인수 테스트")
    public void CREATE_NEW_USER_SUCCESS_TEST() throws Exception{
        //given
        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd!!";
        String phoneNumber = "010-1234-1234";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        //when
        ResultActions result = UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);

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
        String name1 = "alcuk1";
        String userId1 = "alcuk_id";
        String userPassword1 = "alcuk_pwd1";
        String phoneNumber1 = "010-1234-1111";
        UserRequest userCreateRequest1 = UserRequest.builder()
                .name(name1)
                .userId(userId1)
                .userPassword(userPassword1)
                .phoneNumber(phoneNumber1)
                .build();

        String name2 = "alcuk2";
        String userId2 = "alcuk_id";
        String userPassword2 = "alcuk_pwd2";
        String phoneNumber2 = "010-1234-2222";
        UserRequest userCreateRequest2 = UserRequest.builder()
                .name(name2)
                .userId(userId2)
                .userPassword(userPassword2)
                .phoneNumber(phoneNumber2)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest1));
        userCreateRequestList.add(userCreateRequest1);
        ResultActions result = UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest2));
        userCreateRequestList.add(userCreateRequest2);

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
        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "";
        String phoneNumber = "010-1234-1234";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        //when
        ResultActions result = UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);

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
        String name = "";
        String userId = "";
        String userPassword = "";
        String phoneNumber = "";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        //when
        ResultActions result = UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);

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
    @DisplayName("특정 유저 조회 성공 인수 테스트 - 토큰 있을 때")
    public void INQUIRY_USER_WITH_TOKEN_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.inquiryUserWithToken(mvc, userCreateRequest.getUserId(), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.name").value(userCreateRequest.getName()),
                MockMvcResultMatchers.jsonPath("$.user_id").value(userCreateRequest.getUserId()),
                MockMvcResultMatchers.jsonPath("$.phone_number").value(userCreateRequest.getPhoneNumber()),
                MockMvcResultMatchers.jsonPath("$.character").isString(),
                MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.advantage").isNumber(),
                MockMvcResultMatchers.jsonPath("$.penalty").isNumber(),
                MockMvcResultMatchers.jsonPath("$.social_login").isArray()
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("특정 유저 조회 성공 인수 테스트 - 토큰 없을 때")
    public void INQUIRY_USER_WITHOUT_TOKEN_SUCCESS_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";

        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, userCreateRequest.getUserId());

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
    @DisplayName("특정 유저 조회 실패 인수 테스트 - 없는 유저")
    public void INQUIRY_USER_FAIL_CAUSE_UNKNOWN_USER_TEST() throws Exception{
        //given
        String unknownUserId = "unknown_id";

        //when
        ResultActions result = UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, unknownUserId);

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
    @DisplayName("로그인된 유저 조회 성공 인수 테스트")
    public void INQUIRY_USER_ONLINE_SUCCESS_TEST() throws Exception{
        //given
        String token = "mock_token";

        //when
        ResultActions result = UserAcceptanceTestHelper.inquiryUserOnline(mvc, token);

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
    @DisplayName("로그인된 유저 조회 실패 인수 테스트 - 잘못된 토큰")
    public void INQUIRY_USER_ONLINE_FAIL_CAUSE_INVALID_TOKEN_TEST() throws Exception{
        //given
        String invalidToken = "";

        //when
        ResultActions result = UserAcceptanceTestHelper.inquiryUserOnline(mvc, invalidToken);

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
    @DisplayName("로그인된 유저 조회 실패 인수 테스트 - 없는 토큰")
    public void INQUIRY_USER_ONLINE_FAIL_CAUSE_EMPTY_TOKEN_TEST() throws Exception{
        //given

        //when
        ResultActions result = UserAcceptanceTestHelper.inquiryUserOnlineWithNoToken(mvc);

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
    @DisplayName("유저 수정 성공 인수 테스트 - 3가지 수정사항")
    public void MODIFY_USER_3_VALUE_SUCCESS_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
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
        UserRequest modifyUserCreateRequest = UserRequest.builder()
                .name(modifiedName)
                .userPassword(modifiedUserPassword)
                .phoneNumber(modifiedPhoneNumber)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithToken(mvc, token, password, objectMapper.writeValueAsString(modifyUserCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 수정 성공 인수 테스트 - 1가지 수정사항")
    public void MODIFY_USER_1_VALUE_SUCCESS_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String token = "mock_token";
        String password = userCreateRequest.getUserPassword();
        String modifiedName = "alcuk2";
        UserRequest modifyUserCreateRequest = UserRequest.builder()
                .name(modifiedName)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithToken(mvc, token, password, objectMapper.writeValueAsString(modifyUserCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 수정 성공 인수 테스트 - 2가지 수정사항")
    public void MODIFY_USER_2_VALUE_SUCCESS_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String token = "mock_token";
        String password = userCreateRequest.getUserPassword();
        String modifiedName = "alcuk2";
        String modifiedPhoneNumber = "010-1234-1234";
        UserRequest modifyUserCreateRequest = UserRequest.builder()
                .name(modifiedName)
                .phoneNumber(modifiedPhoneNumber)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithToken(mvc, token, password, objectMapper.writeValueAsString(modifyUserCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 수정 실패 인수 테스트 - 헤더에 있는 비밀번호가 저장된 비밀번호 다름")
    public void MODIFY_USER_FAIL_CAUSE_WRONG_PASSWORD_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String token = "mock_token";
        String invalidPassword = "";
        String modifiedName = "alcuk2";
        UserRequest modifyUserCreateRequest = UserRequest.builder()
                .name(modifiedName)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithToken(mvc, token, invalidPassword, objectMapper.writeValueAsString(modifyUserCreateRequest));

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
    @DisplayName("유저 수정 실패 인수 테스트 - 보안 기준 미달 비밀번호로 수정 시도")
    public void MODIFY_USER_FAIL_CAUSE_UNSECURED_PASSWORD_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String token = "mock_token";
        String password = userCreateRequest.getUserPassword();
        String modifiedUserPassword = "";
        UserRequest modifyUserCreateRequest = UserRequest.builder()
                .userPassword(modifiedUserPassword)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithToken(mvc, token, password, objectMapper.writeValueAsString(modifyUserCreateRequest));

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
    @DisplayName("유저 수정 실패 인수 테스트 - 잘못된 토큰")
    public void MODIFY_USER_VALUE_FAIL_INVALID_TOKEN_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String invalidToken = "";
        String password = userCreateRequest.getUserPassword();
        String modifiedName = "alcuk2";
        UserRequest modifyUserCreateRequest = UserRequest.builder()
                .name(modifiedName)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithToken(mvc, invalidToken, password, objectMapper.writeValueAsString(modifyUserCreateRequest));

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
    @DisplayName("유저 수정 실패 인수 테스트 - 없는 토큰")
    public void MODIFY_USER_VALUE_FAIL_EMPTY_TOKEN_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String password = userCreateRequest.getUserPassword();
        String modifiedName = "alcuk2";
        UserRequest modifyUserCreateRequest = UserRequest.builder()
                .name(modifiedName)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithoutToken(mvc, password, objectMapper.writeValueAsString(modifyUserCreateRequest));

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
    @DisplayName("유저 보안 삭제 성공 인수 테스트")
    public void SECURED_DELETE_USER_SUCCESS_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String token = "mock_token";
        String password = userCreateRequest.getUserPassword();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.securedDeleteUserWithToken(mvc, token, password);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 보안 삭제 실패 인수 테스트 - 잘못된 비밀번호")
    public void SECURED_DELETE_USER_FAIL_CAUSE_INVALID_PASSWORD_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String token = "mock_token";
        String invalidPassword = "";

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.securedDeleteUserWithToken(mvc, token, invalidPassword);

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
    @DisplayName("유저 보안 삭제 실패 인수 테스트 - 잘못된 토큰")
    public void SECURED_DELETE_USER_FAIL_CAUSE_INVALID_TOKEN_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String invalidToken = "";
        String password = userCreateRequest.getUserPassword();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.securedDeleteUserWithToken(mvc, invalidToken, password);

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
    @DisplayName("유저 보안 삭제 실패 인수 테스트 - 없는 토큰")
    public void SECURED_DELETE_USER_FAIL_CAUSE_EMPTY_TOKEN_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String password = userCreateRequest.getUserPassword();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.securedDeleteUserWithoutToken(mvc, password);

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
    @DisplayName("유저 강퇴 성공 인수 테스트")
    public void FORCE_DELETE_USER_SUCCESS_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String token = "mock_token";

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        UserResponse userResponse = objectMapper.readValue(
                UserAcceptanceTestHelper.inquiryUserWithToken(mvc, userCreateRequest.getUserId(), token)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), UserResponse.class);
        ResultActions result = UserAcceptanceTestHelper.forcedDeleteUserWithToken(mvc, userResponse.getId(), token);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 강퇴 실패 인수 테스트 - 없는 id")
    public void FORCE_DELETE_USER_FAIL_CAUSE_UNKNOWN_ID_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String token = "mock_token";
        int unknownId = -1;

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.forcedDeleteUserWithToken(mvc, unknownId, token);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown id"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 강퇴 실패 인수 테스트 - 권한 없음")
    public void FORCE_DELETE_USER_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String subjectName = "subject";
        String subjectUserId = "subject_id";
        String subjectUserPassword = "subject_pwd";
        String subjectPhoneNumber = "010-1234-1111";
        UserRequest subjectUserCreateRequest = UserRequest.builder()
                .name(subjectName)
                .userId(subjectUserId)
                .userPassword(subjectUserPassword)
                .build();
        String objectName = "object";
        String objectUserId = "object_id";
        String objectUserPassword = "object_pwd";
        String objectPhoneNumber = "010-1234-2222";
        UserRequest objectUserCreateRequest = UserRequest.builder()
                .name(objectName)
                .userId(objectUserId)
                .userPassword(objectUserPassword)
                .phoneNumber(objectPhoneNumber)
                .build();
        String superToken = "mock_token";

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(objectUserCreateRequest));
        userCreateRequestList.add(subjectUserCreateRequest);
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(subjectUserCreateRequest));
        userCreateRequestList.add(subjectUserCreateRequest);
        UserResponse objectUserResponse = objectMapper.readValue(
                UserAcceptanceTestHelper.inquiryUserWithToken(mvc, objectUserCreateRequest.getUserId(), superToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), UserResponse.class);
        ResultActions result = UserAcceptanceTestHelper.forcedDeleteUserWithToken(mvc, objectUserResponse.getId(), superToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 강퇴 실패 인수 테스트 - 잘못된 토큰")
    public void FORCE_DELETE_USER_FAIL_CAUSE_INVALID_TOKEN_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String token = "";

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        UserResponse userResponse = objectMapper.readValue(
                UserAcceptanceTestHelper.inquiryUserWithToken(mvc, userCreateRequest.getUserId(), token)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), UserResponse.class);
        ResultActions result = UserAcceptanceTestHelper.forcedDeleteUserWithToken(mvc, userResponse.getId(), token);

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
    @DisplayName("유저 강퇴 실패 인수 테스트 - 없는 토큰")
    public void FORCE_DELETE_USER_FAIL_CAUSE_EMPTY_TOKEN_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        UserResponse userResponse = objectMapper.readValue(
                UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, userCreateRequest.getUserId())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), UserResponse.class);
        ResultActions result = UserAcceptanceTestHelper.forcedDeleteUserWithoutToken(mvc, userResponse.getId());

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
    @DisplayName("유저 역할 수정 성공 인수 테스트")
    public void MODIFY_USER_CHARACTER_SUCCESS_TEST() throws Exception{
        //given
        String subjectToken = "mock_token";
        String subjectName = "alcuk2";
        String subjectUserId = "alcuk_id2";
        String subjectUserPassword = "alcuk_pwd2";
        String subjectPhoneNumber = "010-1234-2222";
        UserRequest subjectUserCreateRequest = UserRequest.builder()
                .name(subjectName)
                .userId(subjectUserId)
                .userPassword(subjectUserPassword)
                .phoneNumber(subjectPhoneNumber)
                .build();
        int id = 2;
        String objectName = "alcuk1";
        String objectUserId = "alcuk_id1";
        String objectUserPassword = "alcuk_pwd1";
        String objectPhoneNumber = "010-1234-1111";
        UserRequest objectUserCreateRequest = UserRequest.builder()
                .name(objectName)
                .userId(objectUserId)
                .userPassword(objectUserPassword)
                .phoneNumber(objectPhoneNumber)
                .build();
        String objectCharacter = "object_character";
        UserRequest objectUserCharacterCreateRequest = UserRequest.builder()
                .character(objectCharacter)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(subjectUserCreateRequest));
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(objectUserCreateRequest));
        UserResponse objectUserResponse = objectMapper.readValue(
                UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, objectUserCreateRequest.getUserId())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), UserResponse.class);
        ResultActions result = UserAcceptanceTestHelper.modifyUserCharacter(mvc, objectUserResponse.getId(), subjectToken, objectMapper.writeValueAsString(objectUserCharacterCreateRequest));
        userCreateRequestList.add(objectUserCreateRequest);
        userCreateRequestList.add(subjectUserCreateRequest);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 역할 수정 실패 인수 테스트 - 없는 id")
    public void MODIFY_USER_CHARACTER_FAIL_CAUSE_UNKNOWN_ID_TEST() throws Exception{
        //given
        String subjectToken = "mock_token";
        String subjectName = "alcuk2";
        String subjectUserId = "alcuk_id2";
        String subjectUserPassword = "alcuk_pwd2";
        String subjectPhoneNumber = "010-1234-2222";
        String objectCharacter = "object_character";
        UserRequest objectUserCharacterCreateRequest = UserRequest.builder()
                .character(objectCharacter)
                .build();
        int objectId = 0;
        UserRequest subjectUserCreateRequest = UserRequest.builder()
                .name(subjectName)
                .userId(subjectUserId)
                .userPassword(subjectUserPassword)
                .phoneNumber(subjectPhoneNumber)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(subjectUserCreateRequest));
        ResultActions result = UserAcceptanceTestHelper.modifyUserCharacter(mvc, objectId, subjectToken, objectMapper.writeValueAsString(objectUserCharacterCreateRequest));
        userCreateRequestList.add(subjectUserCreateRequest);

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
    @DisplayName("유저 역할 수정 실패 인수 테스트 - 없는 역할")
    public void MODIFY_USER_CHARACTER_FAIL_CAUSE_UNKNOWN_CHARACTER_TEST() throws Exception{
        //given
        String modifyUrl = "/user/character/{user-name}";
        String subjectToken = "mock_token";
        String subjectName = "alcuk2";
        String subjectUserId = "alcuk_id2";
        String subjectUserPassword = "alcuk_pwd2";
        String subjectPhoneNumber = "010-1234-2222";
        UserRequest subjectUserCreateRequest = UserRequest.builder()
                .name(subjectName)
                .userId(subjectUserId)
                .userPassword(subjectUserPassword)
                .phoneNumber(subjectPhoneNumber)
                .build();
        String createUrl = "/user";
        String objectName = "alcuk1";
        String objectUserId = "alcuk_id1";
        String objectUserPassword = "alcuk_pwd1";
        String objectPhoneNumber = "010-1234-1111";
        UserRequest objectUserCreateRequest = UserRequest.builder()
                .name(objectName)
                .userId(objectUserId)
                .userPassword(objectUserPassword)
                .phoneNumber(objectPhoneNumber)
                .build();
        String objectCharacter = "";
        UserRequest objectUserCharacterCreateRequest = UserRequest.builder()
                .character(objectCharacter)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(objectUserCreateRequest));
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(subjectUserCreateRequest));
        UserResponse objectUserResponse = objectMapper.readValue(
                UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, objectUserCreateRequest.getUserId())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), UserResponse.class);
        ResultActions result = UserAcceptanceTestHelper.modifyUserCharacter(mvc, objectUserResponse.getId(), subjectToken, objectMapper.writeValueAsString(objectUserCharacterCreateRequest));
        userCreateRequestList.add(objectUserCreateRequest);
        userCreateRequestList.add(subjectUserCreateRequest);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown character name"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 역할 수정 실패 인수 테스트 - 권한 없음")
    public void MODIFY_USER_CHARACTER_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String objectName = "alcuk1";
        String objectUserId = "alcuk_id1";
        String objectUserPassword = "alcuk_pwd1";
        String objectPhoneNumber = "010-1234-1111";
        UserRequest objectUserCreateRequest = UserRequest.builder()
                .name(objectName)
                .userId(objectUserId)
                .userPassword(objectUserPassword)
                .phoneNumber(objectPhoneNumber)
                .build();
        String subjectToken = "token";
        String subjectName = "alcuk2";
        String subjectUserId = "alcuk_id2";
        String subjectUserPassword = "alcuk_pwd2";
        String subjectPhoneNumber = "010-1234-2222";
        String objectCharacter = "character";
        UserRequest objectUserCharacterCreateRequest = UserRequest.builder()
                .character(objectCharacter)
                .build();
        UserRequest subjectUserCreateRequest = UserRequest.builder()
                .name(subjectName)
                .userId(subjectUserId)
                .userPassword(subjectUserPassword)
                .phoneNumber(subjectPhoneNumber)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(objectUserCreateRequest));
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(subjectUserCreateRequest));
        UserResponse objectUserResponse = objectMapper.readValue(
                UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, objectUserCreateRequest.getUserId())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), UserResponse.class);
        ResultActions result = UserAcceptanceTestHelper.modifyUserCharacter(mvc, objectUserResponse.getId(), subjectToken, objectMapper.writeValueAsString(objectUserCharacterCreateRequest));
        userCreateRequestList.add(objectUserCreateRequest);
        userCreateRequestList.add(subjectUserCreateRequest);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("전체 유저 조회 성공 인수테스트 - 토큰 있을 때")
    public void INQUIRY_ALL_USER_WITH_TOKEN_SUCCESS_TEST() throws Exception{
        //given
        String name1 = "alcuk1";
        String userId1 = "alcuk_id1";
        String userPassword1 = "alcuk_pwd1";
        String phoneNumber1 = "010-1234-1111";
        String token = "mock_token";
        UserRequest userCreateRequest1 = UserRequest.builder()
                .name(name1)
                .userId(userId1)
                .userPassword(userPassword1)
                .phoneNumber(phoneNumber1)
                .build();
        String name2 = "alcuk2";
        String userId2 = "alcuk_id2";
        String userPassword2 = "alcuk_pwd2";
        String phoneNumber2 = "010-1234-2222";
        UserRequest userCreateRequest2 = UserRequest.builder()
                .name(name2)
                .userId(userId2)
                .userPassword(userPassword2)
                .phoneNumber(phoneNumber2)
                .build();
        String name3 = "alcuk3";
        String userId3 = "alcuk_id3";
        String userPassword3 = "alcuk_pwd3";
        String phoneNumber3 = "010-1234-3333";
        UserRequest userCreateRequest3 = UserRequest.builder()
                .name(name3)
                .userId(userId3)
                .userPassword(userPassword3)
                .phoneNumber(phoneNumber3)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest1));
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest2));
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest3));
        userCreateRequestList.add(userCreateRequest1);
        userCreateRequestList.add(userCreateRequest2);
        userCreateRequestList.add(userCreateRequest3);
        ResultActions result = UserAcceptanceTestHelper.inquiryAllUserWithToken(mvc, 1, 3, token);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(3),
                MockMvcResultMatchers.jsonPath("$.[0].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.[0].name").value(userCreateRequest1.getName()),
                MockMvcResultMatchers.jsonPath("$.[0].user_id").value(userCreateRequest1.getUserId()),
                MockMvcResultMatchers.jsonPath("$.[0].phone_number").value(userCreateRequest1.getPhoneNumber()),
                MockMvcResultMatchers.jsonPath("$.[0].character").isString(),
                MockMvcResultMatchers.jsonPath("$.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.[0].advantage").isNumber(),
                MockMvcResultMatchers.jsonPath("$.[0].penalty").isNumber(),
                MockMvcResultMatchers.jsonPath("$.[0].social_login").isArray(),
                MockMvcResultMatchers.jsonPath("$.[1].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.[1].name").value(userCreateRequest2.getName()),
                MockMvcResultMatchers.jsonPath("$.[1].user_id").value(userCreateRequest2.getUserId()),
                MockMvcResultMatchers.jsonPath("$.[1].phone_number").value(userCreateRequest2.getPhoneNumber()),
                MockMvcResultMatchers.jsonPath("$.[1].character").isString(),
                MockMvcResultMatchers.jsonPath("$.[1].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.[1].advantage").isNumber(),
                MockMvcResultMatchers.jsonPath("$.[1].penalty").isNumber(),
                MockMvcResultMatchers.jsonPath("$.[1].social_login").isArray()
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("전체 유저 조회 실패 인수테스트 - 잘못된 범위")
    public void INQUIRY_ALL_USER_FAIL_INVALID_RANGE_TEST() throws Exception{
        //given
        String name1 = "alcuk1";
        String userId1 = "alcuk_id1";
        String userPassword1 = "alcuk_pwd1";
        String phoneNumber1 = "010-1234-1111";
        String token = "mock_token";
        UserRequest userCreateRequest1 = UserRequest.builder()
                .name(name1)
                .userId(userId1)
                .userPassword(userPassword1)
                .phoneNumber(phoneNumber1)
                .build();
        String name2 = "alcuk2";
        String userId2 = "alcuk_id2";
        String userPassword2 = "alcuk_pwd2";
        String phoneNumber2 = "010-1234-2222";
        UserRequest userCreateRequest2 = UserRequest.builder()
                .name(name2)
                .userId(userId2)
                .userPassword(userPassword2)
                .phoneNumber(phoneNumber2)
                .build();
        String name3 = "alcuk3";
        String userId3 = "alcuk_id3";
        String userPassword3 = "alcuk_pwd3";
        String phoneNumber3 = "010-1234-3333";
        UserRequest userCreateRequest3 = UserRequest.builder()
                .name(name3)
                .userId(userId3)
                .userPassword(userPassword3)
                .phoneNumber(phoneNumber3)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest1));
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest2));
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest3));
        userCreateRequestList.add(userCreateRequest1);
        userCreateRequestList.add(userCreateRequest2);
        userCreateRequestList.add(userCreateRequest3);
        ResultActions result = UserAcceptanceTestHelper.inquiryAllUserWithToken(mvc, 0, 0, token);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid range"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("전체 유저 조회 성공 인수테스트 - 토큰 없을 때")
    public void INQUIRY_ALL_USER_WITHOUT_TOKEN_SUCCESS_TEST() throws Exception{
        //given
        String name1 = "alcuk1";
        String userId1 = "alcuk_id1";
        String userPassword1 = "alcuk_pwd1";
        String phoneNumber1 = "010-1234-1111";
        UserRequest userCreateRequest1 = UserRequest.builder()
                .name(name1)
                .userId(userId1)
                .userPassword(userPassword1)
                .phoneNumber(phoneNumber1)
                .build();
        String name2 = "alcuk2";
        String userId2 = "alcuk_id2";
        String userPassword2 = "alcuk_pwd2";
        String phoneNumber2 = "010-1234-2222";
        UserRequest userCreateRequest2 = UserRequest.builder()
                .name(name2)
                .userId(userId2)
                .userPassword(userPassword2)
                .phoneNumber(phoneNumber2)
                .build();
        String name3 = "alcuk3";
        String userId3 = "alcuk_id3";
        String userPassword3 = "alcuk_pwd3";
        String phoneNumber3 = "010-1234-3333";
        UserRequest userCreateRequest3 = UserRequest.builder()
                .name(name3)
                .userId(userId3)
                .userPassword(userPassword3)
                .phoneNumber(phoneNumber3)
                .build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest1));
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest2));
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest3));
        userCreateRequestList.add(userCreateRequest1);
        userCreateRequestList.add(userCreateRequest2);
        userCreateRequestList.add(userCreateRequest3);
        ResultActions result = UserAcceptanceTestHelper.inquiryAllUserWithoutToken(mvc, 1, 3);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(3),
                MockMvcResultMatchers.jsonPath("$.[0].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.[0].name").value(userCreateRequest1.getName()),
                MockMvcResultMatchers.jsonPath("$.[0].user_id").value(userCreateRequest1.getUserId()),
                MockMvcResultMatchers.jsonPath("$.[0].character").isString(),
                MockMvcResultMatchers.jsonPath("$.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.[1].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.[1].name").value(userCreateRequest2.getName()),
                MockMvcResultMatchers.jsonPath("$.[1].user_id").value(userCreateRequest2.getUserId()),
                MockMvcResultMatchers.jsonPath("$.[1].character").isString(),
                MockMvcResultMatchers.jsonPath("$.[1].created_at").isNotEmpty()
        ).andDo(MockMvcResultHandlers.print());

    }


}
