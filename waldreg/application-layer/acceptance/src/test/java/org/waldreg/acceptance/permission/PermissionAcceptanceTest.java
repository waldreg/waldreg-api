package org.waldreg.acceptance.permission;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class PermissionAcceptanceTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String apiVersion = "1.0";

    @Test
    @DisplayName("새로운 역할 추가 성공 테스트")
    public void CREATE_NEW_CHARACTER_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String url = "/character";
        String token = "mock_token";
        CharacterCreateRequest request = CharacterCreateRequest.builder()
                .characterName("admin_character")
                .permissionList(
                        List.of(
                                PermissionCreateRequest.builder()
                                        .name("attendance_starter")
                                        .status("true")
                                        .build(),
                                PermissionCreateRequest.builder()
                                        .name("team_manager")
                                        .status("false")
                                        .build()
                        )
                ).build();

        // when
        ResultActions result = mvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", "1.0")
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("새로운 역할 추가 실패 테스트 - 최고 관리자가 아닐때")
    public void CREATE_NEW_CHARACTER_FAIL_CAUSE_NOT_ADMIN_ACCEPTANCE_TEST() throws Exception{
        // given
        String url = "/character";
        String token = "failure_token";
        CharacterCreateRequest request = CharacterCreateRequest.builder()
                .characterName("admin_character")
                .permissionList(
                        List.of(
                                PermissionCreateRequest.builder()
                                        .name("attendance_starter")
                                        .status("true")
                                        .build(),
                                PermissionCreateRequest.builder()
                                        .name("team_manager")
                                        .status("false")
                                        .build()
                        )
                ).build();

        // when
        ResultActions result = mvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", "1.0")
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("새로운 역할 추가 실패 테스트 - 잘못된 permission_name 에 대해 요청")
    public void CREATE_NEW_CHARACTER_FAIL_INVALID_PERMISSION_NAME_ACCEPTANCE_TEST()
            throws Exception{
        // given
        String url = "/character";
        String token = "mock_token";
        CharacterCreateRequest request = CharacterCreateRequest.builder()
                .characterName("admin_character")
                .permissionList(
                        List.of(
                                PermissionCreateRequest.builder()
                                        .name("invalid_permission_name")
                                        .status("true")
                                        .build(),
                                PermissionCreateRequest.builder()
                                        .name("team_manager")
                                        .status("false")
                                        .build()
                        )
                ).build();

        // when
        ResultActions result = mvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", "1.0")
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown permission name"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("새로운 역할 추가 실패 테스트 - 잘못된 permission_status 요청")
    public void CREATE_NEW_CHARACTER_FAIL_INVALID_PERMISSION_STATUS_ACCEPTANCE_TEST()
            throws Exception{
        // given
        String url = "/character";
        String token = "mock_token";
        CharacterCreateRequest request = CharacterCreateRequest.builder()
                .characterName("admin_character")
                .permissionList(
                        List.of(
                                PermissionCreateRequest.builder()
                                        .name("attendance_starter")
                                        .status("invalid_status")
                                        .build(),
                                PermissionCreateRequest.builder()
                                        .name("team_manager")
                                        .status("false")
                                        .build()
                        )
                ).build();

        // when
        ResultActions result = mvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", "1.0")
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown permission status"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("새로운 역할 추가 실패 테스트 - 중복된 character_name 추가 요청")
    public void CREATE_NEW_CHARACTER_FAIL_DUPLICATED_CHARACTER_NAME_ACCEPTANCE_TEST()
            throws Exception{
        // given
        String url = "/character";
        String token = "mock_token";
        CharacterCreateRequest request = CharacterCreateRequest.builder()
                .characterName("duplicate")
                .permissionList(List.of()).build();
        CharacterCreateRequest duplicatedRequest = CharacterCreateRequest.builder()
                .characterName("duplicate")
                .permissionList(List.of()).build();

        // when
        mvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", "1.0")
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(objectMapper.writeValueAsString(request)));

        ResultActions duplicatedResult = mvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", "1.0")
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(objectMapper.writeValueAsString(duplicatedRequest)));

        // then
        duplicatedResult.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("Duplicated character name"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    private final static class CharacterCreateRequest{

        @JsonProperty("character_name")
        private String characterName;
        @JsonProperty("permissions")
        private List<PermissionCreateRequest> permissionList;

        public CharacterCreateRequest(){}

        private CharacterCreateRequest(Builder builder){
            this.characterName = builder.characterName;
            this.permissionList = builder.permissionList;
        }

        public static Builder builder(){
            return new Builder();
        }

        public String getCharacterName(){
            return characterName;
        }

        public void setCharacterName(String characterName){
            this.characterName = characterName;
        }

        public List<PermissionCreateRequest> getPermissionList(){
            return permissionList;
        }

        public void setPermissionList(List<PermissionCreateRequest> permissionList){
            this.permissionList = permissionList;
        }

        public final static class Builder{

            private String characterName;
            private List<PermissionCreateRequest> permissionList;

            private Builder(){}

            public Builder characterName(String characterName){
                this.characterName = characterName;
                return this;
            }

            public Builder permissionList(List<PermissionCreateRequest> permissionList){
                this.permissionList = permissionList;
                return this;
            }

            public CharacterCreateRequest build(){
                return new CharacterCreateRequest(this);
            }

        }

    }

    private final static class PermissionCreateRequest{

        @JsonProperty("permission_name")
        private String name;
        @JsonProperty("permission_status")
        private String status;

        public PermissionCreateRequest(){}

        private PermissionCreateRequest(Builder builder){
            this.name = builder.name;
            this.status = builder.status;
        }

        public static Builder builder(){
            return new Builder();
        }

        public String getName(){
            return name;
        }

        public void setName(String name){
            this.name = name;
        }

        public String getStatus(){
            return status;
        }

        public void setStatus(String status){
            this.status = status;
        }

        private final static class Builder{

            private String name;
            private String status;

            private Builder(){}

            public Builder name(String name){
                this.name = name;
                return this;
            }

            public Builder status(String status){
                this.status = status;
                return this;
            }

            public PermissionCreateRequest build(){
                return new PermissionCreateRequest(this);
            }

        }

    }

}
