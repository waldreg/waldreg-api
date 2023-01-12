package org.waldreg.acceptance.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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
    
    static String apiVersion="1.0";

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
