package org.waldreg.acceptance.authentication;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.waldreg.acceptance.user.UserAcceptanceTestHelper;
import org.waldreg.controller.user.request.UserRequest;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationAcceptanceTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String apiVersion = "1.0";


    @Test
    @DisplayName("토큰 발급 요청 성공")
    public void CREATE_TOKEN_PUBLISH_REQUEST_TEST() throws Exception{
        //given
        String userId = "Fixtar";
        String userPassword = "1234abcd@";
        TokenCreateRequest tokenCreateRequest = TokenCreateRequest.builder()
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

        //when
        ResultActions result = AuthenticationAcceptanceTestHelper.authenticateByUserIdAndUserPassword(mvc,objectMapper.writeValueAsString(tokenCreateRequest));

        //then
        result.andExpectAll(MockMvcResultMatchers.status().isOk(),
                            MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE,
                                                                  "application/json"),
                            MockMvcResultMatchers.jsonPath("$.access_token").isString(),
                            MockMvcResultMatchers.jsonPath("$.token_type").value("Bearer"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("없는 유저 토큰 발급 요청 실패")
    public void CREATE_TOKEN_PUBLISH_REQUEST_NOT_MATCHED_USER_TEST() throws Exception{
        //given
        String userId = "nbafsda";
        String userPassword = "1234abcd@";
        TokenCreateRequest tokenCreateRequest = TokenCreateRequest.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build();

        //when
        ResultActions result = AuthenticationAcceptanceTestHelper.authenticateByUserIdAndUserPassword(mvc,objectMapper.writeValueAsString(tokenCreateRequest));

        //then
        result.andExpectAll(MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE,
                                                                  "application/json"),
                            MockMvcResultMatchers.jsonPath("$.messages").value("Unknown user id"),
                            MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("토큰 발급 실패 잘못된 인증 정보 (잘못된 비밀번호)")
    public void TOKEN_PUBLISH_FAIL_INVALID_INFORMATION() throws Exception{
        //given
        String userId = "alcuk";
        String userPassword = "1234bfre@";
        TokenCreateRequest tokenCreateRequest = TokenCreateRequest.builder()
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
        //when
        ResultActions result = AuthenticationAcceptanceTestHelper.authenticateByUserIdAndUserPassword(mvc,objectMapper.writeValueAsString(tokenCreateRequest));
        //then
        result.andExpectAll(MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE,
                                                                  "application/json"),
                            MockMvcResultMatchers.header().string("api-version", apiVersion),
                            MockMvcResultMatchers.jsonPath("$.messages").value(
                                    "Invalid authentication information"),
                            MockMvcResultMatchers.jsonPath("$.document_url").value(
                                    "docs.waldreg.org"))
                .andDo(MockMvcResultHandlers.print());

    }


    private static final class TokenCreateRequest{

        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("user_password")
        private String userPassword;

        public TokenCreateRequest(){}

        private TokenCreateRequest(Builder builder){
            this.userId = builder.userId;
            this.userPassword = builder.userPassword;
        }

        public static Builder builder(){
            return new Builder();
        }

        public static final class Builder{

            private String userId;
            private String userPassword;

            private Builder(){}

            public Builder userId(String userId){
                this.userId = userId;
                return this;
            }

            public Builder userPassword(String userPassword){
                this.userPassword = userPassword;
                return this;
            }

            public TokenCreateRequest build(){
                return new TokenCreateRequest(this);
            }

        }

    }

}
