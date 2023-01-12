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
    @DisplayName("새로운 역할 추가 성공 인수 테스트")
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
        ResultActions result = PermissionAcceptanceTestHelper
                .createCharacter(mvc, url, token, objectMapper.writeValueAsString(request));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("새로운 역할 추가 실패 인수 테스트 - 최고 관리자가 아닐때")
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
        ResultActions result = PermissionAcceptanceTestHelper
                .createCharacter(mvc, url, token, objectMapper.writeValueAsString(request));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("새로운 역할 추가 실패 인수 테스트 - 잘못된 permission_name 에 대해 요청")
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
        ResultActions result = PermissionAcceptanceTestHelper
                .createCharacter(mvc, url, token, objectMapper.writeValueAsString(request));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown permission name"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("새로운 역할 추가 실패 인수 테스트 - 잘못된 permission_status 요청")
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
        ResultActions result = PermissionAcceptanceTestHelper
                .createCharacter(mvc, url, token, objectMapper.writeValueAsString(request));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown permission status"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("새로운 역할 추가 실패 인수 테스트 - 중복된 character_name 추가 요청")
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
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, url, token, objectMapper.writeValueAsString(request));

        ResultActions duplicatedResult = PermissionAcceptanceTestHelper
                .createCharacter(mvc, url, token,
                        objectMapper.writeValueAsString(duplicatedRequest));

        // then
        duplicatedResult.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("Duplicated character name"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("역할 목록 조회 성공 인수 테스트")
    public void INQUIRY_CHARACTER_LIST_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String url = "/character";
        String token = "mock_token";
        String characterName = "mock_acceptance";
        CharacterCreateRequest request = CharacterCreateRequest.builder()
                .characterName(characterName)
                .permissionList(List.of()).build();

        // when
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, url, token, objectMapper.writeValueAsString(request));

        ResultActions result = PermissionAcceptanceTestHelper.inquiryCharacterList(mvc, url, token);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.character_name.[0]").value(characterName)
        );
    }

    @Test
    @DisplayName("역할 목록 조회 실패 인수 테스트 - 최고 관리자가 아님")
    public void INQUIRY_CHARACTER_LIST_FAIL_NOT_ADMIN_ACCEPTANCE_TEST() throws Exception{
        // given
        String url = "/character";
        String token = "not_admin_token";

        // when
        ResultActions result = PermissionAcceptanceTestHelper.inquiryCharacterList(mvc, url, token);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 역할 조회 성공 인수 테스트")
    public void INQUIRY_CHARACTER_BY_NAME_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String url = "/character/{character-name}";
        String token = "mock_token";
        String characterName = "mock_character";
        String permissionName = "mock_permission";
        String permissionStatus = "mock_permission_status";
        CharacterCreateRequest request = CharacterCreateRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionCreateRequest.builder()
                                .name(permissionName)
                                .status(permissionStatus)
                                .build()
                ))
                .build();

        // when
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, "/character", token,
                        objectMapper.writeValueAsString(request));

        ResultActions result = PermissionAcceptanceTestHelper
                .inquirySpecificCharacter(mvc, url, characterName, token);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.character_name").value(characterName),
                MockMvcResultMatchers.jsonPath("$.permissions.[0].permission_name")
                        .value(permissionName),
                MockMvcResultMatchers.jsonPath("$.permissions.[0].permission_status")
                        .value(permissionStatus)
        );
    }

    @Test
    @DisplayName("특정 역할 조회 실패 인수테스트 - 최고 관리자가 아님")
    public void INQUIRY_CHARACTER_BY_NAME_FAIL_NOT_ADMIN_ACCEPTANCE_TEST() throws Exception{
        // given
        String url = "/character/{character-name}";
        String token = "not_admin_token";
        String characterName = "mock_character_name";

        // when
        ResultActions result = PermissionAcceptanceTestHelper
                .inquirySpecificCharacter(mvc, url, characterName, token);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 역할 조회 실패 인수테스트 - 잘못된 character-name 으로 조회")
    public void INQUIRY_CHARACTER_BY_NAME_FAIL_WRONG_CHARACTER_NAME_ACCEPTANCE_TEST()
            throws Exception{
        // given
        String url = "/character/{character-name}";
        String token = "mock_token";
        String characterName = "unknown_character_name";

        // when
        ResultActions result = PermissionAcceptanceTestHelper
                .inquirySpecificCharacter(mvc, url, characterName, token);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown character name"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 역할 수정 성공 인수테스트")
    public void MODIFY_CHARACTER_BY_NAME_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String url = "/character/{character-name}";
        String token = "mock_token";
        String characterName = "mock_character_name";
        String permissionName = "mock_permission_name";
        String permissionStatus = "mock_permission_status";
        CharacterCreateRequest request = CharacterCreateRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionCreateRequest.builder()
                                .name(permissionName)
                                .status(permissionStatus)
                                .build()
                )).build();

        // when
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, "/character", token,
                        objectMapper.writeValueAsString(request));

        request.characterName = "modified_character_name";

        ResultActions result = PermissionAcceptanceTestHelper
                .modifySpecificCharacter(mvc, url, characterName, token,
                        objectMapper.writeValueAsString(request));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("특정 역할 수정 실패 인수테스트 - 최고 관리자가 아님")
    public void MODIFY_CHARACTER_BY_NAME_FAIL_NOT_ADMIN_ACCEPTANCE_TEST() throws Exception{
        // given
        String url = "/character/{character-name}";
        String token = "not_admin_token";
        String characterName = "mock_character_name";
        CharacterCreateRequest request = CharacterCreateRequest.builder()
                .characterName(characterName)
                .permissionList(List.of()).build();

        // when
        ResultActions result = PermissionAcceptanceTestHelper
                .modifySpecificCharacter(mvc, url, characterName, token,
                        objectMapper.writeValueAsString(request));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 역할 수정 실패 인수테스트 - 수정할 이름 중복")
    public void MODIFY_CHARACTER_BY_NAME_FAIL_DUPLICATED_NAME_ACCEPTANCE_TEST() throws Exception{
        // given
        String createUrl = "/character";
        String changeUrl = "/character/{character-name}";
        String token = "mock_token";
        String beforeCharacterName = "name_before_duplicated";
        String characterName = "duplicated_character";
        CharacterCreateRequest request = CharacterCreateRequest.builder()
                .characterName(characterName)
                .permissionList(List.of()).build();
        CharacterCreateRequest request2 = CharacterCreateRequest.builder()
                .characterName(beforeCharacterName)
                .permissionList(List.of()).build();

        // when
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, createUrl, token, objectMapper.writeValueAsString(request));

        PermissionAcceptanceTestHelper
                .createCharacter(mvc, createUrl, token, objectMapper.writeValueAsString(request2));

        request2.characterName = characterName;

        ResultActions result = PermissionAcceptanceTestHelper.modifySpecificCharacter(
                mvc, changeUrl, beforeCharacterName, token,
                objectMapper.writeValueAsString(request2));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("Duplicated character-name"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 역할 수정 실패 인수테스트 - path-parameter 에 잘못된 character-name 으로 수정 요청")
    public void MODIFY_CHARACTER_BY_NAME_FAIL_INVALID_PATH_NAME_ACCEPTANCE_TEST() throws Exception{
        // given
        String url = "/character/{character-name}";
        String characterName = "unknown character name";
        String token = "mock_token";
        CharacterCreateRequest request = CharacterCreateRequest.builder()
                .characterName(characterName)
                .permissionList(List.of()).build();

        // when
        ResultActions result = PermissionAcceptanceTestHelper.modifySpecificCharacter(
                mvc, url, characterName, token,
                objectMapper.writeValueAsString(request));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown character name"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 역할 수정 실패 인수테스트 - 없는 permission_name 에 대해서 요청")
    public void MODIFY_CHARACTER_BY_NAME_FAIL_INVALID_PERMISSION_NAME_ACCEPTANCE_TEST()
            throws Exception{
        // given
        String url = "/character/{character-name}";
        String createUrl = "/character";
        String token = "mock_token";
        String characterName = "mock_character_name";
        String permissionName = "mock_permission_name";
        String permissionStatus = "mock_permission_status";
        CharacterCreateRequest request = CharacterCreateRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionCreateRequest.builder()
                                .name(permissionName)
                                .status(permissionStatus)
                                .build()
                )).build();
        CharacterCreateRequest changeRequest = CharacterCreateRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionCreateRequest.builder()
                                .name("invalid_permission_name")
                                .status(permissionStatus)
                                .build()
                )).build();

        // when
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, createUrl, token, objectMapper.writeValueAsString(request));

        ResultActions result = PermissionAcceptanceTestHelper.modifySpecificCharacter(
                mvc, url, characterName, token,
                objectMapper.writeValueAsString(changeRequest));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown permission name"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 역할 수정 실패 인수테스트 - 잘못된 permission_status 로 수정 요청")
    public void MODIFY_CHARACTER_BY_NAME_FAIL_INVALID_PERMISSION_STATUS_ACCEPTANCE_TEST()
            throws Exception{
        // given
        String url = "/character/{character-name}";
        String createUrl = "/character";
        String token = "mock_token";
        String characterName = "mock_character_name";
        String permissionName = "mock_permission_name";
        String permissionStatus = "mock_permission_status";
        CharacterCreateRequest request = CharacterCreateRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionCreateRequest.builder()
                                .name(permissionName)
                                .status(permissionStatus)
                                .build()
                )).build();
        CharacterCreateRequest changeRequest = CharacterCreateRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionCreateRequest.builder()
                                .name(permissionName)
                                .status("wrong_status")
                                .build()
                )).build();

        // when
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, createUrl, token, objectMapper.writeValueAsString(request));

        ResultActions result = PermissionAcceptanceTestHelper.modifySpecificCharacter(
                mvc, url, characterName, token,
                objectMapper.writeValueAsString(changeRequest));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown permission status"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("선택가능한 permission 목록 조회 성공 인수 테스트")
    public void INQUIRY_PERMISSION_LIST_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String url = "/permission";
        String token = "mock_token";

        // when
        ResultActions result = mvc.perform(MockMvcRequestBuilders
                .get(url)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.permissions").isArray()
        );
    }

    @Test
    @DisplayName("선택 가능한 permission 목록 조회 실패 인수 테스트 - 최고 관리자 아님")
    public void INQUIRY_PERMISSION_LIST_FAIL_NOT_ADMIN_ACCEPTANCE_TEST() throws Exception{
        // given
        String url = "/permission";
        String token = "not_admin_token";

        // when
        ResultActions result = mvc.perform(MockMvcRequestBuilders
                .get(url)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("역할 삭제 성공 인수 테스트")
    public void DELETE_CHARACTER_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String url = "/permission";
        String deleteUrl = "/permission/{character-name}";
        String characterName = "mock_character";
        String token = "mock_token";
        CharacterCreateRequest request = CharacterCreateRequest.builder()
                .characterName(characterName)
                .permissionList(List.of()).build();

        // when
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, url, token, objectMapper.writeValueAsString(request));

        ResultActions result = PermissionAcceptanceTestHelper
                .deleteSpecificCharacter(mvc, deleteUrl, characterName, token);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("역할 삭제 실패 - 최고 관리자가 아님")
    public void DELETE_CHARACTER_FAIL_NOT_ADMIN_TEST() throws Exception{
        // given
        String url = "/permission";
        String deleteUrl = "/permission/{character-name}";
        String characterName = "mock_character";
        String token = "mock_token";
        String notAdminToken = "not_admin_token";
        CharacterCreateRequest request = CharacterCreateRequest.builder()
                .characterName(characterName)
                .permissionList(List.of()).build();

        // when
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, url, token, objectMapper.writeValueAsString(request));

        ResultActions result = PermissionAcceptanceTestHelper
                .deleteSpecificCharacter(mvc, deleteUrl, characterName, notAdminToken);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("역할 삭제 실패 인수 테스트 - 없는 역할에 대한 삭제 요청")
    public void DELETE_CHARACTER_FAIL_INVALID_CHARACTER_ACCEPTANCE_TEST() throws Exception{
        // given
        String url = "/permission";
        String deleteUrl = "/permission/{character-name}";
        String characterName = "mock_character";
        String token = "mock_token";

        // when
        ResultActions result = PermissionAcceptanceTestHelper
                .deleteSpecificCharacter(mvc, deleteUrl, characterName, token);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown permission name"),
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
