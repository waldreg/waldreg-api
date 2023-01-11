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
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("유저 생성 성공 테스트")
    public void CREATE_NEW_USER_SUCCESS_TEST() throws Exception{
        //given
        String url = "/user";
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name("alcuk")
                .userId("alcuk_id")
                .userPassword("alcuk_pwd")
                .phoneNumber("010-1234-1234")
                .build();

        //when
        ResultActions result = mvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version","1.0")
                .content(objectMapper.writeValueAsString(userCreateRequest)));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version","1.0")
        ).andDo(MockMvcResultHandlers.print());
    }

    private static class UserCreateRequest{

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

        public static class Builder{

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
