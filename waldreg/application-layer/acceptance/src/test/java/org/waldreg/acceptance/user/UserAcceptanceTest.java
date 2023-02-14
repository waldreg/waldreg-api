package org.waldreg.acceptance.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.waldreg.acceptance.authentication.AuthenticationAcceptanceTestHelper;
import org.waldreg.acceptance.permission.PermissionAcceptanceTestHelper;
import org.waldreg.auth.request.AuthTokenRequest;
import org.waldreg.controller.user.request.CharacterRequest;
import org.waldreg.controller.user.request.UpdateUserRequest;
import org.waldreg.controller.user.request.UserRequest;
import org.waldreg.controller.user.response.UserListResponse;
import org.waldreg.controller.user.response.UserResponse;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserAcceptanceTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String apiVersion = "1.0";

    private final ArrayList<UserRequest> userCreateRequestList = new ArrayList<>();

    private final List<String> deleteWaitCharacterList = new ArrayList<>();

    @BeforeEach
    @AfterEach
    public void INITIAL_PERMISSION() throws Exception{
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        for (String characterName : deleteWaitCharacterList){
            PermissionAcceptanceTestHelper.deleteSpecificCharacter(mvc, characterName, token);
            PermissionAcceptanceTestHelper.inquirySpecificCharacter(mvc, characterName, token)
                    .andExpectAll(
                            MockMvcResultMatchers
                                    .status().isBadRequest(),
                            MockMvcResultMatchers
                                    .content().contentType(MediaType.APPLICATION_JSON),
                            MockMvcResultMatchers
                                    .header()
                                    .string(HttpHeaders.CONTENT_TYPE, "application/json"),
                            MockMvcResultMatchers
                                    .header().string("Api-version", apiVersion),
                            MockMvcResultMatchers
                                    .jsonPath("$.code")
                                            .value("CHARACTER-420"),
                            MockMvcResultMatchers
                                    .jsonPath("$.messages")
                                    .value("Can not find character named \"" + characterName + "\""),
                            MockMvcResultMatchers
                                    .jsonPath("$.document_url")
                                    .value("docs.waldreg.org")
                    );
        }
        deleteWaitCharacterList.clear();
    }

    @BeforeEach
    @AfterEach
    public void INITIATE() throws Exception{
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
                    MockMvcResultMatchers.jsonPath("$.code").value("USER-406"),
                    MockMvcResultMatchers.jsonPath("$.messages").value("Unknown user_id \""+request.getUserId()+"\""),
                    MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
            );
        }
        userCreateRequestList.clear();
    }

    @Test
    @DisplayName("유저 생성 성공 인수 테스트")
    public void CREATE_NEW_USER_SUCCESS_TEST() throws Exception{
        //given
        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
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
        String userPassword1 = "2gdddddd!";
        String phoneNumber1 = "010-1234-1111";
        UserRequest userCreateRequest1 = UserRequest.builder()
                .name(name1)
                .userId(userId1)
                .userPassword(userPassword1)
                .phoneNumber(phoneNumber1)
                .build();

        String name2 = "alcuk2";
        String userId2 = "alcuk_id";
        String userPassword2 = "2gdddddd!";
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
                MockMvcResultMatchers.jsonPath("$.code").value("USER-400"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Duplicated user_id \""+userId1+"\""),
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
                MockMvcResultMatchers.jsonPath("$.code").value("USER-401"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unsecured user_password input"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 생성 실패 인수 테스트 - 잘못된 이름 입력")
    public void CREATE_NEW_USER_FAIL_CAUSE_INVALID_NAME_INPUT() throws Exception{
        //given
        String name = "";
        String userId = "alcuk123";
        String userPassword = "2dddddddd!";
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
                MockMvcResultMatchers.jsonPath("$.code").value("USER-402"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid name input"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 생성 실패 인수 테스트 - 잘못된 전화번호 입력")
    public void CREATE_NEW_USER_FAIL_CAUSE_INVALID_PHONE_NUMBER_INPUT() throws Exception{
        //given
        String name = "미쳐버려";
        String userId = "alcuk123";
        String userPassword = "2dddddddd!";
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
                MockMvcResultMatchers.jsonPath("$.code").value("USER-405"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid phone_number input"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 생성 실패 인수 테스트 - 잘못된 user_id 입력")
    public void CREATE_NEW_USER_FAIL_CAUSE_INVALID_USER_ID_INPUT() throws Exception{
        //given
        String name = "alcuk12345";
        String userId = "       ";
        String userPassword = "2dddddddd!";
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
                MockMvcResultMatchers.jsonPath("$.code").value("USER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid user_id input"),
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
        String userPassword = "alcuk_pwd1sdfq!";
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
                MockMvcResultMatchers.jsonPath("$.reward_point").isNumber(),
                MockMvcResultMatchers.jsonPath("$.social_login").isArray()
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("특정 유저 조회 성공 인수 테스트 - 토큰 없을 때")
    public void INQUIRY_USER_WITHOUT_TOKEN_SUCCESS_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1234!";
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
                MockMvcResultMatchers.jsonPath("$.code").value("USER-406"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown user_id \""+unknownUserId+"\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("로그인된 유저 조회 성공 인수 테스트")
    public void INQUIRY_USER_ONLINE_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        //when
        ResultActions result = UserAcceptanceTestHelper.inquiryUserOnline(mvc, adminToken);

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
                MockMvcResultMatchers.jsonPath("$.reward_point").isNumber(),
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
                MockMvcResultMatchers.jsonPath("$.code").value("AUTH-402"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Authenticate fail"),
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
                MockMvcResultMatchers.jsonPath("$.code").value("AUTH-402"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Authenticate fail"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 수정 성공 인수 테스트")
    public void MODIFY_USER_SUCCESS_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcud1234!";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String password = userCreateRequest.getUserPassword();
        String modifiedName = "alcuk2";
        String modifiedUserPassword = "alcud2234!";
        String modifiedPhoneNumber = "010-1234-1234";
        UpdateUserRequest modifyUserCreateRequest = UpdateUserRequest.builder()
                .name(modifiedName)
                .userPassword(modifiedUserPassword)
                .phoneNumber(modifiedPhoneNumber)
                .build();

        //when
        String token = createUserAndGetToken(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithToken(mvc, token, password, objectMapper.writeValueAsString(modifyUserCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(Integer.MAX_VALUE)
    @DisplayName("관리자 수정 성공 인수 테스트")
    public void MODIFY_ADMIN_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String adminPassword = "0000";
        String modifiedName = "Admin";
        String modifiedUserPassword = "alcuk2234!";
        String modifiedPhoneNumber = "010-0000-0000";
        UpdateUserRequest modifyUserCreateRequest = UpdateUserRequest.builder()
                .name(modifiedName)
                .userPassword(modifiedUserPassword)
                .phoneNumber(modifiedPhoneNumber)
                .build();
        String modifiedName2 = "Admin";
        String modifiedUserPassword2 = "alcuk2233!";
        String modifiedPhoneNumber2 = "010-0000-0000";
        UpdateUserRequest modifyUserCreateRequest2 = UpdateUserRequest.builder()
                .name(modifiedName2)
                .userPassword(modifiedUserPassword2)
                .phoneNumber(modifiedPhoneNumber2)
                .build();

        //when
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithToken(mvc, adminToken, adminPassword, objectMapper.writeValueAsString(modifyUserCreateRequest));
        ResultActions result2 = UserAcceptanceTestHelper.modifyUserWithToken(mvc, adminToken, modifiedUserPassword, objectMapper.writeValueAsString(modifyUserCreateRequest2));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());

        result2.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 수정 실패 인수 테스트 - 헤더에 있는 비밀번호가 저장된 비밀번호 다름")
    public void MODIFY_USER_FAIL_CAUSE_WRONG_PASSWORD_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1!";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String invalidPassword = "1234ff!";
        String modifiedName = "alcuk2";
        String modifiedUserPassword = "alcud2234!";
        String modifiedPhoneNumber = "010-1234-1234";
        UpdateUserRequest modifyUserCreateRequest = UpdateUserRequest.builder()
                .name(modifiedName)
                .userPassword(modifiedUserPassword)
                .phoneNumber(modifiedPhoneNumber)
                .build();

        //when
        String token = createUserAndGetToken(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithToken(mvc, token, invalidPassword, objectMapper.writeValueAsString(modifyUserCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("AUTH-402"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Authenticate fail"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 수정 실패 인수 테스트 - 보안 기준 미달 비밀번호로 수정 시도")
    public void MODIFY_USER_FAIL_CAUSE_UNSECURED_PASSWORD_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcukpwd1!";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String modifiedName = "alcuk2";
        String modifiedUserPassword = "alu";
        String modifiedPhoneNumber = "010-1234-1234";
        UpdateUserRequest modifyUserCreateRequest = UpdateUserRequest.builder()
                .name(modifiedName)
                .userPassword(modifiedUserPassword)
                .phoneNumber(modifiedPhoneNumber)
                .build();

        //when
        String token = createUserAndGetToken(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.modifyUserWithToken(mvc, token, userPassword, objectMapper.writeValueAsString(modifyUserCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("USER-401"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unsecured user_password input"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 수정 실패 인수 테스트 - 잘못된 토큰")
    public void MODIFY_USER_VALUE_FAIL_INVALID_TOKEN_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1!";
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
        String modifiedUserPassword = "alcud2234!";
        String modifiedPhoneNumber = "010-1234-1234";
        UpdateUserRequest modifyUserCreateRequest = UpdateUserRequest.builder()
                .name(modifiedName)
                .userPassword(modifiedUserPassword)
                .phoneNumber(modifiedPhoneNumber)
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
                MockMvcResultMatchers.jsonPath("$.code").value("AUTH-402"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Authenticate fail"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 수정 실패 인수 테스트 - 없는 토큰")
    public void MODIFY_USER_VALUE_FAIL_EMPTY_TOKEN_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd!1";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String password = userCreateRequest.getUserPassword();
        String modifiedName = "alcuk2";
        String modifiedUserPassword = "alcud2234!";
        String modifiedPhoneNumber = "010-1234-1234";
        UpdateUserRequest modifyUserCreateRequest = UpdateUserRequest.builder()
                .name(modifiedName)
                .userPassword(modifiedUserPassword)
                .phoneNumber(modifiedPhoneNumber)
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
                MockMvcResultMatchers.jsonPath("$.code").value("AUTH-402"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Authenticate fail"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 보안 삭제 성공 인수 테스트")
    public void SECURED_DELETE_USER_SUCCESS_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1!";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String password = userCreateRequest.getUserPassword();

        //when
        String token = createUserAndGetToken(userCreateRequest);
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
        String userPassword = "alcuk_pwd1!";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String invalidPassword = "123s4!";

        //when
        String token = createUserAndGetToken(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.securedDeleteUserWithToken(mvc, token, invalidPassword);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("AUTH-402"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Authenticate fail"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 보안 삭제 실패 인수 테스트 - 잘못된 토큰")
    public void SECURED_DELETE_USER_FAIL_CAUSE_INVALID_TOKEN_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1!";
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
                MockMvcResultMatchers.jsonPath("$.code").value("AUTH-402"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Authenticate fail"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 보안 삭제 실패 인수 테스트 - 없는 토큰")
    public void SECURED_DELETE_USER_FAIL_CAUSE_EMPTY_TOKEN_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1!";
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
                MockMvcResultMatchers.jsonPath("$.code").value("AUTH-402"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Authenticate fail"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 강퇴 성공 인수 테스트")
    public void FORCE_DELETE_USER_SUCCESS_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1!";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        UserResponse userResponse = objectMapper.readValue(
                UserAcceptanceTestHelper.inquiryUserWithToken(mvc, userCreateRequest.getUserId(), adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), UserResponse.class);
        ResultActions result = UserAcceptanceTestHelper.forcedDeleteUserWithToken(mvc, userResponse.getId(), adminToken);

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
        String userPassword = "alcuk_pwd1!";
        String phoneNumber = "010-1234-1111";
        UserRequest userCreateRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int unknownId = 999;

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest));
        userCreateRequestList.add(userCreateRequest);
        ResultActions result = UserAcceptanceTestHelper.forcedDeleteUserWithToken(mvc, unknownId, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("USER-408"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown id \""+unknownId+"\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 강퇴 실패 인수 테스트 - 권한 없음")
    public void FORCE_DELETE_USER_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String subjectName = "subject";
        String subjectUserId = "subject_id";
        String subjectUserPassword = "subject_pwd!!";
        String subjectPhoneNumber = "010-1234-1111";
        UserRequest subjectUserCreateRequest = UserRequest.builder()
                .name(subjectName)
                .userId(subjectUserId)
                .userPassword(subjectUserPassword)
                .phoneNumber(subjectPhoneNumber)
                .build();
        String objectName = "object";
        String objectUserId = "id123";
        String objectUserPassword = "objectwd123!!";
        String objectPhoneNumber = "010-1234-2222";
        UserRequest objectUserCreateRequest = UserRequest.builder()
                .name(objectName)
                .userId(objectUserId)
                .userPassword(objectUserPassword)
                .phoneNumber(objectPhoneNumber)
                .build();

        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        //when
        String token = createUserAndGetToken(objectUserCreateRequest);
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(subjectUserCreateRequest));
        userCreateRequestList.add(subjectUserCreateRequest);
        UserResponse objectUserResponse = objectMapper.readValue(
                UserAcceptanceTestHelper.inquiryUserWithToken(mvc, objectUserCreateRequest.getUserId(), adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), UserResponse.class);
        ResultActions result = UserAcceptanceTestHelper.forcedDeleteUserWithToken(mvc, objectUserResponse.getId(), token);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
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
        String userPassword = "alcuk_pwd123!";
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
                MockMvcResultMatchers.jsonPath("$.code").value("AUTH-402"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Authenticate fail"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 강퇴 실패 인수 테스트 - 없는 토큰")
    public void FORCE_DELETE_USER_FAIL_CAUSE_EMPTY_TOKEN_TEST() throws Exception{
        //given
        String name = "alcuk1";
        String userId = "alcuk_id";
        String userPassword = "alcuk_pwd1123!";
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
                MockMvcResultMatchers.jsonPath("$.code").value("AUTH-402"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Authenticate fail"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유저 역할 수정 성공 인수 테스트")
    public void MODIFY_USER_CHARACTER_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String objectName = "alcuk1";
        String objectUserId = "alcuk_id1";
        String objectUserPassword = "alcuk_pwd1!";
        String objectPhoneNumber = "010-1234-1111";
        UserRequest objectUserCreateRequest = UserRequest.builder()
                .name(objectName)
                .userId(objectUserId)
                .userPassword(objectUserPassword)
                .phoneNumber(objectPhoneNumber)
                .build();
        String objectCharacter = "object_character";
        CharacterRequest characterRequest = new CharacterRequest();
        characterRequest.setCharacter(objectCharacter);
        org.waldreg.controller.character.request.CharacterRequest request = org.waldreg.controller.character.request.CharacterRequest.builder()
                .characterName(objectCharacter)
                .permissionList(List.of()).build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(objectUserCreateRequest));
        userCreateRequestList.add(objectUserCreateRequest);
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, adminToken, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(objectCharacter);
        UserResponse objectUserResponse = objectMapper.readValue(
                UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, objectUserCreateRequest.getUserId())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), UserResponse.class);
        UserAcceptanceTestHelper.modifyUserCharacter(mvc, objectUserResponse.getId(), adminToken, objectMapper.writeValueAsString(characterRequest));
        ResultActions result = UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, objectUserCreateRequest.getUserId());

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 역할 수정 시 동기화 성공 인수 테스트")
    public void MODIFY_USER_CHARACTER_AND_SYNCHRONIZE_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String name1 = "alcuk1";
        String userId1 = "alcuk_id1";
        String userPassword1 = "alcuk_pwd1!";
        String phoneNumber1 = "010-1234-1111";
        UserRequest userCreateRequest1 = UserRequest.builder()
                .name(name1)
                .userId(userId1)
                .userPassword(userPassword1)
                .phoneNumber(phoneNumber1)
                .build();
        String name2 = "alcuk2";
        String userId2 = "alcuk_id2";
        String userPassword2 = "alcuk_pwd2!";
        String phoneNumber2 = "010-1234-2222";
        UserRequest userCreateRequest2 = UserRequest.builder()
                .name(name2)
                .userId(userId2)
                .userPassword(userPassword2)
                .phoneNumber(phoneNumber2)
                .build();
        String objectCharacter = "object_character";
        CharacterRequest characterRequest = new CharacterRequest();
        characterRequest.setCharacter(objectCharacter);
        org.waldreg.controller.character.request.CharacterRequest request = org.waldreg.controller.character.request.CharacterRequest.builder()
                .characterName(objectCharacter)
                .permissionList(List.of()).build();
        String objectCharacter2 = "object_character2";
        CharacterRequest characterRequest2 = new CharacterRequest();
        characterRequest.setCharacter(objectCharacter2);
        org.waldreg.controller.character.request.CharacterRequest request2 = org.waldreg.controller.character.request.CharacterRequest.builder()
                .characterName(objectCharacter2)
                .permissionList(List.of()).build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest1));
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userCreateRequest2));
        userCreateRequestList.add(userCreateRequest1);
        userCreateRequestList.add(userCreateRequest2);
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, adminToken, objectMapper.writeValueAsString(request));
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, adminToken, objectMapper.writeValueAsString(request2));
        deleteWaitCharacterList.add(objectCharacter);
        deleteWaitCharacterList.add(objectCharacter2);
        UserResponse objectUserResponse = objectMapper.readValue(
                UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, userCreateRequest1.getUserId())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), UserResponse.class);
        UserResponse objectUserResponse2 = objectMapper.readValue(
                UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, userCreateRequest2.getUserId())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), UserResponse.class);
        UserAcceptanceTestHelper.modifyUserCharacter(mvc, objectUserResponse.getId(), adminToken, objectMapper.writeValueAsString(characterRequest));
        UserAcceptanceTestHelper.modifyUserCharacter(mvc, objectUserResponse2.getId(), adminToken, objectMapper.writeValueAsString(characterRequest));
        UserAcceptanceTestHelper.modifyUserCharacter(mvc, objectUserResponse2.getId(), adminToken, objectMapper.writeValueAsString(characterRequest2));
        ResultActions result = UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, userCreateRequest1.getUserId());

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.name").value(userCreateRequest1.getName()),
                MockMvcResultMatchers.jsonPath("$.user_id").value(userCreateRequest1.getUserId()),
                MockMvcResultMatchers.jsonPath("$.character").value(request2.getCharacterName()),
                MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty()
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 역할 수정 실패 인수 테스트 - 없는 id")
    public void MODIFY_USER_CHARACTER_FAIL_CAUSE_UNKNOWN_ID_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String subjectName = "alcuk2";
        String subjectUserId = "alcuk_id2";
        String subjectUserPassword = "alcuk_pwd2!!";
        String subjectPhoneNumber = "010-1234-2222";
        String subjectCharacter = "object_character";
        CharacterRequest characterRequest = new CharacterRequest();
        characterRequest.setCharacter(subjectCharacter);
        int subjectId = 999;
        UserRequest subjectUserCreateRequest = UserRequest.builder()
                .name(subjectName)
                .userId(subjectUserId)
                .userPassword(subjectUserPassword)
                .phoneNumber(subjectPhoneNumber)
                .build();
        org.waldreg.controller.character.request.CharacterRequest request = org.waldreg.controller.character.request.CharacterRequest.builder()
                .characterName(subjectCharacter)
                .permissionList(List.of()).build();

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(subjectUserCreateRequest));
        userCreateRequestList.add(subjectUserCreateRequest);
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, adminToken, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(subjectCharacter);
        ResultActions result = UserAcceptanceTestHelper.modifyUserCharacter(mvc, subjectId, adminToken, objectMapper.writeValueAsString(characterRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("USER-408"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown id \""+subjectId+"\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 역할 수정 실패 인수 테스트 - 없는 역할")
    public void MODIFY_USER_CHARACTER_FAIL_CAUSE_UNKNOWN_CHARACTER_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String subjectName = "alcuk2";
        String subjectUserId = "alcuk_id2";
        String subjectUserPassword = "alcuk_pwd2!!";
        String subjectPhoneNumber = "010-1234-2222";
        UserRequest subjectUserCreateRequest = UserRequest.builder()
                .name(subjectName)
                .userId(subjectUserId)
                .userPassword(subjectUserPassword)
                .phoneNumber(subjectPhoneNumber)
                .build();
        String objectCharacter = "ddvdvdvfd";
        CharacterRequest characterRequest = new CharacterRequest();
        characterRequest.setCharacter(objectCharacter);

        //when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(subjectUserCreateRequest));
        userCreateRequestList.add(subjectUserCreateRequest);
        UserResponse subjectUserResponse = objectMapper.readValue(
                UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, subjectUserCreateRequest.getUserId())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), UserResponse.class);
        ResultActions result = UserAcceptanceTestHelper.modifyUserCharacter(mvc, subjectUserResponse.getId(), adminToken, objectMapper.writeValueAsString(characterRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-420"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Can not find character named \"" + characterRequest.getCharacter() + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 역할 수정 실패 인수 테스트 - 권한 없음")
    public void MODIFY_USER_CHARACTER_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String objectName = "alcuk1";
        String objectUserId = "alcuk_id1";
        String objectUserPassword = "alcuk_pwd1!";
        String objectPhoneNumber = "010-1234-1111";
        UserRequest objectUserCreateRequest = UserRequest.builder()
                .name(objectName)
                .userId(objectUserId)
                .userPassword(objectUserPassword)
                .phoneNumber(objectPhoneNumber)
                .build();
        String objectCharacter = "character";
        CharacterRequest characterRequest = new CharacterRequest();
        characterRequest.setCharacter(objectCharacter);
        org.waldreg.controller.character.request.CharacterRequest request = org.waldreg.controller.character.request.CharacterRequest.builder()
                .characterName(objectCharacter)
                .permissionList(List.of()).build();

        //when
        String token = createUserAndGetToken(objectUserCreateRequest);
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, token, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(objectCharacter);
        UserResponse objectUserResponse = objectMapper.readValue(
                UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, objectUserCreateRequest.getUserId())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), UserResponse.class);
        ResultActions result = UserAcceptanceTestHelper.modifyUserCharacter(mvc, objectUserResponse.getId(), token, objectMapper.writeValueAsString(characterRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
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
        String userPassword1 = "alcuk_pwd1!";
        String phoneNumber1 = "010-1234-1111";
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        UserRequest userCreateRequest1 = UserRequest.builder()
                .name(name1)
                .userId(userId1)
                .userPassword(userPassword1)
                .phoneNumber(phoneNumber1)
                .build();
        String name2 = "alcuk2";
        String userId2 = "alcuk_id2";
        String userPassword2 = "alcuk_pwd2!";
        String phoneNumber2 = "010-1234-2222";
        UserRequest userCreateRequest2 = UserRequest.builder()
                .name(name2)
                .userId(userId2)
                .userPassword(userPassword2)
                .phoneNumber(phoneNumber2)
                .build();
        String name3 = "alcuk3";
        String userId3 = "alcuk_id3";
        String userPassword3 = "alcuk_pwd3!";
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
        ResultActions result = UserAcceptanceTestHelper.inquiryAllUserWithToken(mvc, 1, 4, adminToken);
        UserListResponse userList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), UserListResponse.class);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(4),
                MockMvcResultMatchers.jsonPath("$.users.[0].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.users.[0].name").value(userCreateRequest2.getName()),
                MockMvcResultMatchers.jsonPath("$.users.[0].user_id").value(userCreateRequest2.getUserId()),
                MockMvcResultMatchers.jsonPath("$.users.[0].phone_number").value(userCreateRequest2.getPhoneNumber()),
                MockMvcResultMatchers.jsonPath("$.users.[0].character").isString(),
                MockMvcResultMatchers.jsonPath("$.users.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.users.[0].reward_point").isNumber(),
                MockMvcResultMatchers.jsonPath("$.users.[0].social_login").isArray(),
                MockMvcResultMatchers.jsonPath("$.users.[1].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.users.[1].name").value(userCreateRequest3.getName()),
                MockMvcResultMatchers.jsonPath("$.users.[1].user_id").value(userCreateRequest3.getUserId()),
                MockMvcResultMatchers.jsonPath("$.users.[1].phone_number").value(userCreateRequest3.getPhoneNumber()),
                MockMvcResultMatchers.jsonPath("$.users.[1].character").isString(),
                MockMvcResultMatchers.jsonPath("$.users.[1].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.users.[1].reward_point").isNumber(),
                MockMvcResultMatchers.jsonPath("$.users.[1].social_login").isArray(),
                MockMvcResultMatchers.jsonPath("$.users.[2].name").value(userCreateRequest1.getName()),
                MockMvcResultMatchers.jsonPath("$.users.[2].user_id").value(userCreateRequest1.getUserId()),
                MockMvcResultMatchers.jsonPath("$.users.[2].phone_number").value(userCreateRequest1.getPhoneNumber()),
                MockMvcResultMatchers.jsonPath("$.users.[2].character").isString(),
                MockMvcResultMatchers.jsonPath("$.users.[2].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.users.[2].reward_point").isNumber(),
                MockMvcResultMatchers.jsonPath("$.users.[2].social_login").isArray()
        ).andDo(MockMvcResultHandlers.print());

    }
    @Test
    @DisplayName("전체 유저 조회 성공 인수테스트 - 토큰 있을 때, 범위 없이")
    public void INQUIRY_ALL_USER_WITH_TOKEN_WITHOUT_RANGE_SUCCESS_TEST() throws Exception{
        //given
        String name1 = "alcuk1";
        String userId1 = "alcuk_id1";
        String userPassword1 = "alcuk_pwd1!";
        String phoneNumber1 = "010-1234-1111";
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        UserRequest userCreateRequest1 = UserRequest.builder()
                .name(name1)
                .userId(userId1)
                .userPassword(userPassword1)
                .phoneNumber(phoneNumber1)
                .build();
        String name2 = "alcuk2";
        String userId2 = "alcuk_id2";
        String userPassword2 = "alcuk_pwd2!";
        String phoneNumber2 = "010-1234-2222";
        UserRequest userCreateRequest2 = UserRequest.builder()
                .name(name2)
                .userId(userId2)
                .userPassword(userPassword2)
                .phoneNumber(phoneNumber2)
                .build();
        String name3 = "alcuk3";
        String userId3 = "alcuk_id3";
        String userPassword3 = "alcuk_pwd3!";
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
        ResultActions result = mvc.perform(MockMvcRequestBuilders
                                                 .get("/users")
                                                 .accept(MediaType.APPLICATION_JSON)
                                                 .header("api-version", apiVersion)
                                                 .header(HttpHeaders.AUTHORIZATION, adminToken));

        UserListResponse userList = objectMapper.readValue(result
                                                                   .andReturn()
                                                                   .getResponse()
                                                                   .getContentAsString(), UserListResponse.class);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(4),
                MockMvcResultMatchers.jsonPath("$.users.[0].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.users.[0].name").value(userCreateRequest2.getName()),
                MockMvcResultMatchers.jsonPath("$.users.[0].user_id").value(userCreateRequest2.getUserId()),
                MockMvcResultMatchers.jsonPath("$.users.[0].phone_number").value(userCreateRequest2.getPhoneNumber()),
                MockMvcResultMatchers.jsonPath("$.users.[0].character").isString(),
                MockMvcResultMatchers.jsonPath("$.users.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.users.[0].reward_point").isNumber(),
                MockMvcResultMatchers.jsonPath("$.users.[0].social_login").isArray(),
                MockMvcResultMatchers.jsonPath("$.users.[1].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.users.[1].name").value(userCreateRequest3.getName()),
                MockMvcResultMatchers.jsonPath("$.users.[1].user_id").value(userCreateRequest3.getUserId()),
                MockMvcResultMatchers.jsonPath("$.users.[1].phone_number").value(userCreateRequest3.getPhoneNumber()),
                MockMvcResultMatchers.jsonPath("$.users.[1].character").isString(),
                MockMvcResultMatchers.jsonPath("$.users.[1].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.users.[1].reward_point").isNumber(),
                MockMvcResultMatchers.jsonPath("$.users.[1].social_login").isArray(),
                MockMvcResultMatchers.jsonPath("$.users.[2].name").value(userCreateRequest1.getName()),
                MockMvcResultMatchers.jsonPath("$.users.[2].user_id").value(userCreateRequest1.getUserId()),
                MockMvcResultMatchers.jsonPath("$.users.[2].phone_number").value(userCreateRequest1.getPhoneNumber()),
                MockMvcResultMatchers.jsonPath("$.users.[2].character").isString(),
                MockMvcResultMatchers.jsonPath("$.users.[2].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.users.[2].reward_point").isNumber(),
                MockMvcResultMatchers.jsonPath("$.users.[2].social_login").isArray()
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
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
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
        ResultActions result = UserAcceptanceTestHelper.inquiryAllUserWithToken(mvc, 0, 0, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("USER-407"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid range start-idx \""+0+"\", end-idx \""+0+"\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("전체 유저 조회 성공 인수테스트 - 토큰 없을 때")
    public void INQUIRY_ALL_USER_WITHOUT_TOKEN_SUCCESS_TEST() throws Exception{
        //given
        String name1 = "alcuk1";
        String userId1 = "alcuk_id1";
        String userPassword1 = "alcuk_pwd1!";
        String phoneNumber1 = "010-1234-1111";
        UserRequest userCreateRequest1 = UserRequest.builder()
                .name(name1)
                .userId(userId1)
                .userPassword(userPassword1)
                .phoneNumber(phoneNumber1)
                .build();
        String name2 = "alcuk2";
        String userId2 = "alcuk_id2";
        String userPassword2 = "alcuk_pwd2!";
        String phoneNumber2 = "010-1234-2222";
        UserRequest userCreateRequest2 = UserRequest.builder()
                .name(name2)
                .userId(userId2)
                .userPassword(userPassword2)
                .phoneNumber(phoneNumber2)
                .build();
        String name3 = "alcuk3";
        String userId3 = "alcuk_id3";
        String userPassword3 = "alcuk_pwd3!";
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
        ResultActions result = UserAcceptanceTestHelper.inquiryAllUserWithoutToken(mvc, 1, 2);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(4),
                MockMvcResultMatchers.jsonPath("$.users.[0].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.users.[0].name").value(userCreateRequest2.getName()),
                MockMvcResultMatchers.jsonPath("$.users.[0].user_id").value(userCreateRequest2.getUserId()),
                MockMvcResultMatchers.jsonPath("$.users.[0].character").isString(),
                MockMvcResultMatchers.jsonPath("$.users.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.users.[1].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.users.[1].name").value(userCreateRequest3.getName()),
                MockMvcResultMatchers.jsonPath("$.users.[1].user_id").value(userCreateRequest3.getUserId()),
                MockMvcResultMatchers.jsonPath("$.users.[1].character").isString(),
                MockMvcResultMatchers.jsonPath("$.users.[1].created_at").isNotEmpty()
        ).andDo(MockMvcResultHandlers.print());

    }

    private String createUserAndGetToken(UserRequest userRequest) throws Exception{
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest));
        userCreateRequestList.add(userRequest);

        return AuthenticationAcceptanceTestHelper.getToken(mvc, objectMapper, AuthTokenRequest.builder()
                .userId(userRequest.getUserId())
                .userPassword(userRequest.getUserPassword())
                .build());
    }

}
