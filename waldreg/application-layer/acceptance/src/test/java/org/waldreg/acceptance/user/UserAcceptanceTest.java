package org.waldreg.acceptance.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class UserAcceptanceTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;
<<<<<<< HEAD

    private final String apiVersion="1.0";
=======
    
    static String apiVersion="1.0";
>>>>>>> b6bf8cc7ac540b9e4376ccb6e353dff9e74eb818

    @Test
    @DisplayName("유저 생성 성공 테스트")
    public void CREATE_NEW_USER_SUCCESS_TEST() throws Exception{
        //given
        String url = "/user";
        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword="alcuk_pwd";
        String phoneNumber="010-1234-1234";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        //when
        ResultActions result = mvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version",apiVersion)
                .content(objectMapper.writeValueAsString(userCreateRequest)));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version",apiVersion)
        ).andDo(MockMvcResultHandlers.print());
    }

<<<<<<< HEAD

    @Test
    @DisplayName("유저 생성 실패 인수 테스트 - 중복 아이디")
    public void CREATE_NEW_USER_FAIL_CAUSE_DUPLICATE_USER_ID() throws Exception{
        //given
        String url = "/user";
        String name1 = "alcuk1";
        String userId1 = "alcuk_id";
        String userPassword1="alcuk_pwd1";
        String phoneNumber1="010-1234-1111";
        UserCreateRequest userCreateRequest1 = UserCreateRequest.builder()
                .name(name1)
                .userId(userId1)
                .userPassword(userPassword1)
                .phoneNumber(phoneNumber1)
                .build();

        String name2 = "alcuk2";
        String userId2 = "alcuk_id";
        String userPassword2="alcuk_pwd2";
        String phoneNumber2="010-1234-2222";
        UserCreateRequest userCreateRequest2 = UserCreateRequest.builder()
                .name(name2)
                .userId(userId2)
                .userPassword(userPassword2)
                .phoneNumber(phoneNumber2)
                .build();

        //when
        mvc.perform(MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("api-version",apiVersion)
                .content(objectMapper.writeValueAsString(userCreateRequest1)));

        ResultActions result=mvc.perform(MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("api-version",apiVersion)
                .content(objectMapper.writeValueAsString(userCreateRequest2)));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE,"application/json"),
                MockMvcResultMatchers.header().string("api-version",apiVersion),
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
        String userPassword="";
        String phoneNumber="010-1234-1234";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        //when
        ResultActions result = mvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version",apiVersion)
                .content(objectMapper.writeValueAsString(userCreateRequest)));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE,"application/json"),
                MockMvcResultMatchers.header().string("api-version",apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unsecured user_password"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("유저 생성 실패 인수 테스트 - 비밀번호 보안 기준 미달")
    public void CREATE_NEW_USER_FAIL_CAUSE_INVALID_REQUEST() throws Exception{
        //given
        String url = "/user";
        String name = "";
        String userId = "";
        String userPassword="alcuk_pwd";
        String phoneNumber="010-1234-1234";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        //when
        ResultActions result=mvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version",apiVersion)
                .content(objectMapper.writeValueAsString(userCreateRequest)));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE,"application/json"),
                MockMvcResultMatchers.header().string("api-version",apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid request"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

=======
>>>>>>> b6bf8cc7ac540b9e4376ccb6e353dff9e74eb818
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

<<<<<<< HEAD
        public void setName(String name){this.name = name;}

=======
>>>>>>> b6bf8cc7ac540b9e4376ccb6e353dff9e74eb818
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

            public Builder userPassword(String userPassward){
                this.userPassword = userPassward;
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
