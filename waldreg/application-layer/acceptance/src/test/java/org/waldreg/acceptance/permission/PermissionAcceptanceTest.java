package org.waldreg.acceptance.permission;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
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
import org.waldreg.controller.character.request.CharacterRequest;
import org.waldreg.controller.character.request.PermissionRequest;

@SpringBootTest
@AutoConfigureMockMvc
public class PermissionAcceptanceTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;
    private final String adminId = "Admin";
    private final String adminPassword = "0000";
    private final String apiVersion = "1.0";
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
                                    .header().string("api-version", apiVersion),
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

    @Test
    @DisplayName("새로운 역할 추가 성공 인수 테스트")
    public void CREATE_NEW_CHARACTER_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String characterName = "new character";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(
                        List.of(
                                PermissionRequest.builder()
                                        .name("Character manager")
                                        .status("true")
                                        .build(),
                                PermissionRequest.builder()
                                        .name("Fire other user permission")
                                        .status("false")
                                        .build()
                        )
                ).build();

        // when
        ResultActions result = PermissionAcceptanceTestHelper
                .createCharacter(mvc, token, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(characterName);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("새로운 역할 추가 실패 인수 테스트 - 최고 관리자가 아닐때")
    public void CREATE_NEW_CHARACTER_FAIL_CAUSE_NOT_ADMIN_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = "failure_token";
        String characterName = "admin_character";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(
                        List.of(
                                PermissionRequest.builder()
                                        .name("attendance_starter")
                                        .status("true")
                                        .build(),
                                PermissionRequest.builder()
                                        .name("team_manager")
                                        .status("false")
                                        .build()
                        )
                ).build();

        // when
        ResultActions result = PermissionAcceptanceTestHelper
                .createCharacter(mvc, token, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(characterName);

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
        String token = "mock_token";
        String characterName = "admin_character";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(
                        List.of(
                                PermissionRequest.builder()
                                        .name("invalid_permission_name")
                                        .status("true")
                                        .build(),
                                PermissionRequest.builder()
                                        .name("team_manager")
                                        .status("false")
                                        .build()
                        )
                ).build();

        // when
        ResultActions result = PermissionAcceptanceTestHelper
                .createCharacter(mvc, token, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(characterName);

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
        String token = "mock_token";
        String characterName = "admin_character";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(
                        List.of(
                                PermissionRequest.builder()
                                        .name("attendance_starter")
                                        .status("invalid_status")
                                        .build(),
                                PermissionRequest.builder()
                                        .name("team_manager")
                                        .status("false")
                                        .build()
                        )
                ).build();

        // when
        ResultActions result = PermissionAcceptanceTestHelper
                .createCharacter(mvc, token, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(characterName);

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
        String token = "mock_token";
        String characterName = "duplicate";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of()).build();
        CharacterRequest duplicatedRequest = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of()).build();

        // when
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, token, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(characterName);

        ResultActions duplicatedResult = PermissionAcceptanceTestHelper
                .createCharacter(mvc, token, objectMapper.writeValueAsString(duplicatedRequest));

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
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String characterName = "mock_acceptance";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of()).build();

        // when
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, token, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(characterName);

        ResultActions result = PermissionAcceptanceTestHelper.inquiryCharacterList(mvc, token);

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
        ResultActions result = PermissionAcceptanceTestHelper.inquiryCharacterList(mvc, token);

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
        String token = "mock_token";
        String characterName = "mock_character";
        String permissionName = "mock_permission";
        String permissionStatus = "mock_permission_status";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionRequest.builder()
                                .name(permissionName)
                                .status(permissionStatus)
                                .build()
                ))
                .build();

        // when
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, token, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(characterName);

        ResultActions result = PermissionAcceptanceTestHelper
                .inquirySpecificCharacter(mvc, characterName, token);

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
        String token = "not_admin_token";
        String characterName = "mock_character_name";

        // when
        ResultActions result = PermissionAcceptanceTestHelper
                .inquirySpecificCharacter(mvc, characterName, token);

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
        String token = "mock_token";
        String characterName = "unknown_character_name";

        // when
        ResultActions result = PermissionAcceptanceTestHelper
                .inquirySpecificCharacter(mvc, characterName, token);

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
        String token = "mock_token";
        String characterName = "mock_character_name";
        String permissionName = "mock_permission_name";
        String permissionStatus = "mock_permission_status";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionRequest.builder()
                                .name(permissionName)
                                .status(permissionStatus)
                                .build()
                )).build();

        CharacterRequest modifiedRequest = CharacterRequest.builder()
                .characterName("modified name")
                .permissionList(List.of(
                        PermissionRequest.builder()
                                .name(permissionName)
                                .status(permissionStatus)
                                .build()
                )).build();

        // when
        PermissionAcceptanceTestHelper.createCharacter(mvc, token, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(characterName);

        ResultActions result = PermissionAcceptanceTestHelper
                .modifySpecificCharacter(mvc, characterName, token, objectMapper.writeValueAsString(modifiedRequest));

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
        String token = "not_admin_token";
        String characterName = "mock_character_name";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of()).build();

        // when
        ResultActions result = PermissionAcceptanceTestHelper
                .modifySpecificCharacter(mvc, characterName, token, objectMapper.writeValueAsString(request));

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
        String token = "mock_token";
        String beforeCharacterName = "name_before_duplicated";
        String characterName = "duplicated_character";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of()).build();
        CharacterRequest request2 = CharacterRequest.builder()
                .characterName(beforeCharacterName)
                .permissionList(List.of()).build();
        CharacterRequest modifedRequest = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of()).build();

        // when
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, token, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(characterName);

        PermissionAcceptanceTestHelper
                .createCharacter(mvc, token, objectMapper.writeValueAsString(request2));
        deleteWaitCharacterList.add(beforeCharacterName);

        ResultActions result = PermissionAcceptanceTestHelper
                .modifySpecificCharacter(mvc, beforeCharacterName, token, objectMapper.writeValueAsString(modifedRequest));

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
        String characterName = "unknown character name";
        String token = "mock_token";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of()).build();

        // when
        ResultActions result = PermissionAcceptanceTestHelper
                .modifySpecificCharacter(mvc, characterName, token, objectMapper.writeValueAsString(request));

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
        String token = "mock_token";
        String characterName = "mock_character_name";
        String permissionName = "mock_permission_name";
        String permissionStatus = "mock_permission_status";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionRequest.builder()
                                .name(permissionName)
                                .status(permissionStatus)
                                .build()
                )).build();
        CharacterRequest changeRequest = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionRequest.builder()
                                .name("invalid_permission_name")
                                .status(permissionStatus)
                                .build()
                )).build();

        // when
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, token, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(characterName);

        ResultActions result = PermissionAcceptanceTestHelper
                .modifySpecificCharacter(mvc, characterName, token, objectMapper.writeValueAsString(changeRequest));

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
        String token = "mock_token";
        String characterName = "mock_character_name";
        String permissionName = "mock_permission_name";
        String permissionStatus = "mock_permission_status";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionRequest.builder()
                                .name(permissionName)
                                .status(permissionStatus)
                                .build()
                )).build();
        CharacterRequest changeRequest = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionRequest.builder()
                                .name(permissionName)
                                .status("wrong_status")
                                .build()
                )).build();

        // when
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, token, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(characterName);

        ResultActions result = PermissionAcceptanceTestHelper
                .modifySpecificCharacter(mvc, characterName, token, objectMapper.writeValueAsString(changeRequest));

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
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        // when
        ResultActions result = PermissionAcceptanceTestHelper.inquiryPermissionList(mvc, token);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.permissions").isArray()
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("선택 가능한 permission 목록 조회 실패 인수 테스트 - 최고 관리자 아님")
    public void INQUIRY_PERMISSION_LIST_FAIL_NOT_ADMIN_ACCEPTANCE_TEST() throws Exception{
        // given
        String url = "/permission";
        String token = "not_admin_token";

        // when
        ResultActions result = PermissionAcceptanceTestHelper.inquiryPermissionList(mvc, token);

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
        String characterName = "mock_character";
        String token = "mock_token";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of()).build();

        // when
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, token, objectMapper.writeValueAsString(request));

        ResultActions result = PermissionAcceptanceTestHelper
                .deleteSpecificCharacter(mvc, characterName, token);

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
        String characterName = "mock_character";
        String token = "mock_token";
        String notAdminToken = "not_admin_token";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of()).build();

        // when
        PermissionAcceptanceTestHelper
                .createCharacter(mvc, token, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(characterName);

        ResultActions result = PermissionAcceptanceTestHelper
                .deleteSpecificCharacter(mvc, characterName, notAdminToken);

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
        String characterName = "mock_character";
        String token = "mock_token";

        // when
        ResultActions result = PermissionAcceptanceTestHelper
                .deleteSpecificCharacter(mvc, characterName, token);

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

}
