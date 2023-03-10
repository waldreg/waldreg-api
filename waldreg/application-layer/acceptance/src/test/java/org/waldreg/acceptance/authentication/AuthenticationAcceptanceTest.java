package org.waldreg.acceptance.authentication;

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
import org.waldreg.acceptance.user.UserAcceptanceTestHelper;
import org.waldreg.auth.request.AuthTokenRequest;
import org.waldreg.controller.joiningpool.request.UserRequest;
import org.waldreg.controller.user.response.UserResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationAcceptanceTest{

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
                    MockMvcResultMatchers.jsonPath("$.messages").value("Unknown user_id \"" + request.getUserId() + "\""),
                    MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
            ).andDo(MockMvcResultHandlers.print());
        }
        userCreateRequestList.clear();
    }

    @Test
    @DisplayName("?????? ?????? ?????? ??????")
    public void CREATE_TOKEN_PUBLISH_REQUEST_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String userId = "Fixtar";
        String userPassword = "1234abcd@";
        AuthTokenRequest tokenCreateRequest = AuthTokenRequest.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build();

        String name2 = "alcuk";
        String userId2 = "Fixtar";
        String userPassword2 = "1234abcd@";
        String phoneNumber2 = "010-1234-1111";
        UserRequest userRequest = UserRequest.builder()
                .name(name2)
                .userId(userId2)
                .userPassword(userPassword2)
                .phoneNumber(phoneNumber2)
                .build();
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest));
        UserAcceptanceTestHelper.approveJoinRequest(mvc, adminToken, userRequest.getUserId());
        userCreateRequestList.add(userRequest);

        //when
        ResultActions result = AuthenticationAcceptanceTestHelper.authenticateByUserIdAndUserPassword(mvc, objectMapper.writeValueAsString(tokenCreateRequest));

        //then
        result.andExpectAll(MockMvcResultMatchers.status().isOk(),
                            MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE,
                                                                  "application/json"),
                            MockMvcResultMatchers.jsonPath("$.access_token").isString(),
                            MockMvcResultMatchers.jsonPath("$.token_type").value("Bearer"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ??????")
    public void CREATE_TOKEN_PUBLISH_REQUEST_NOT_MATCHED_USER_TEST() throws Exception{
        //given
        String userId = "nbafsda";
        String userPassword = "1234abcd@";
        AuthTokenRequest tokenCreateRequest = AuthTokenRequest.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build();

        //when
        ResultActions result = AuthenticationAcceptanceTestHelper.authenticateByUserIdAndUserPassword(mvc, objectMapper.writeValueAsString(tokenCreateRequest));

        //then
        result.andExpectAll(MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                            MockMvcResultMatchers.jsonPath("$.code").value("AUTH-404"),
                            MockMvcResultMatchers.jsonPath("$.messages").value("Invalid authentication information"),
                            MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("?????? ???????????? ?????? ?????? ?????? ?????? ??????")
    public void CREATE_TOKEN_PUBLISH_REQUEST_EMPTY_USERID_TEST() throws Exception{
        //given
        String userId = "    ";
        String userPassword = "1234abcd@";
        AuthTokenRequest tokenCreateRequest = AuthTokenRequest.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build();

        //when
        ResultActions result = AuthenticationAcceptanceTestHelper.authenticateByUserIdAndUserPassword(mvc, objectMapper.writeValueAsString(tokenCreateRequest));

        //then
        result.andExpectAll(MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE,
                                                                  "application/json"),
                            MockMvcResultMatchers.jsonPath("$.code").value("AUTH-403"),
                            MockMvcResultMatchers.jsonPath("$.messages").value("user_id cannot be blank"),
                            MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? ?????? ?????? (????????? ????????????)")
    public void TOKEN_PUBLISH_FAIL_INVALID_INFORMATION() throws Exception{
        //given
        String userId = "alcuk";
        String userPassword = "1234bfre@";
        AuthTokenRequest tokenCreateRequest = AuthTokenRequest.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build();

        String name2 = "alcuk2";
        String userId2 = "alcuk";
        String userPassword2 = "1234abcd@";
        String phoneNumber2 = "010-1234-1111";
        UserRequest userRequest = UserRequest.builder()
                .name(name2)
                .userId(userId2)
                .userPassword(userPassword2)
                .phoneNumber(phoneNumber2)
                .build();
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest));
        userCreateRequestList.add(userRequest);
        //when
        ResultActions result = AuthenticationAcceptanceTestHelper.authenticateByUserIdAndUserPassword(mvc, objectMapper.writeValueAsString(tokenCreateRequest));
        //then
        result.andExpectAll(MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE,
                                                                  "application/json"),
                            MockMvcResultMatchers.header().string("api-version", apiVersion),
                            MockMvcResultMatchers.jsonPath("$.code").value("AUTH-404"),
                            MockMvcResultMatchers.jsonPath("$.messages").value(
                                    "Invalid authentication information"),
                            MockMvcResultMatchers.jsonPath("$.document_url").value(
                                    "docs.waldreg.org"))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("????????? ???????????? ?????? ??????")
    public void CREATE_TEMPORARY_TOKEN_SUCCESS_TEST() throws Exception{
        //given
        String name2 = "alcuk";
        String userId2 = "Fixtar";
        String userPassword2 = "1234abcd@";
        String token = createUserAndGetToken(name2, userId2, userPassword2);

        //when
        ResultActions result = AuthenticationAcceptanceTestHelper.authenticateByToken(mvc, token);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE,
                                                      "application/json"),
                MockMvcResultMatchers.jsonPath("$.temporary_token").isString(),
                MockMvcResultMatchers.jsonPath("$.token_type").value("Bearer")
        );

    }

    @Test
    @DisplayName("????????? ???????????? ?????? ?????? - ????????? ??????")
    public void CREATE_TEMPORARY_TOKEN_FAIL_UNKNOWN_ID_TEST() throws Exception{
        //given
        String name2 = "alcuk";
        String userId2 = "Fixtar";
        String userPassword2 = "1234abcd@";
        String token = createUserAndGetToken(name2, userId2, userPassword2);
        //when
        ResultActions result = AuthenticationAcceptanceTestHelper.authenticateByToken(mvc, "token");
        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.code").value("AUTH-402"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Authenticate fail"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );

    }

    private String createUserAndGetToken(String name, String userId, String userPassword) throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        UserRequest userRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber("010-1234-1234")
                .build();
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest));
        UserAcceptanceTestHelper.approveJoinRequest(mvc, adminToken, userRequest.getUserId());
        userCreateRequestList.add(userRequest);

        return AuthenticationAcceptanceTestHelper.getToken(mvc, objectMapper, AuthTokenRequest.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build());
    }

}
