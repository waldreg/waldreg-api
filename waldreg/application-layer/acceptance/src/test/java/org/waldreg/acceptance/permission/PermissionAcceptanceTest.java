package org.waldreg.acceptance.permission;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import org.waldreg.acceptance.user.UserAcceptanceTestHelper;
import org.waldreg.auth.request.AuthTokenRequest;
import org.waldreg.controller.character.request.CharacterRequest;
import org.waldreg.controller.character.request.PermissionRequest;
import org.waldreg.controller.character.response.PermissionResponse;
import org.waldreg.controller.user.request.UserRequest;
import org.waldreg.controller.user.response.UserResponse;

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
    private final ArrayList<UserRequest> userCreateRequestList = new ArrayList<>();

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
    public void INITIATE_USER() throws Exception{
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
                    MockMvcResultMatchers.jsonPath("$.messages").value("Unknown user_id"),
                    MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
            );
        }
        userCreateRequestList.clear();
    }

    @Test
    @DisplayName("새로운 역할 추가 성공 인수 테스트")
    public void CREATE_NEW_CHARACTER_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        List<PermissionResponse> permissionResponseList = getPermissionResponseList();
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String characterName = "new character";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(
                        List.of(
                                PermissionRequest.builder()
                                        .id(permissionResponseList.get(0).getId())
                                        .name(permissionResponseList.get(0).getName())
                                        .status(permissionResponseList.get(0).getStatusList().get(0))
                                        .build(),
                                PermissionRequest.builder()
                                        .id(permissionResponseList.get(1).getId())
                                        .name(permissionResponseList.get(1).getName())
                                        .status(permissionResponseList.get(1).getStatusList().get(0))
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
        List<PermissionResponse> permissionResponseList = getPermissionResponseList();
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        String characterName = "something new character";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(
                        List.of(
                                PermissionRequest.builder()
                                        .id(permissionResponseList.get(0).getId())
                                        .name(permissionResponseList.get(0).getName())
                                        .status(permissionResponseList.get(0).getStatusList().get(0))
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
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("새로운 역할 추가 실패 인수 테스트 - 잘못된 permission_name 에 대해 요청")
    public void CREATE_NEW_CHARACTER_FAIL_INVALID_PERMISSION_NAME_ACCEPTANCE_TEST()
            throws Exception{
        // given
        List<PermissionResponse> permissionResponseList = getPermissionResponseList();
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String characterName = "New manager";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(
                        List.of(
                                PermissionRequest.builder()
                                        .id(permissionResponseList.get(0).getId())
                                        .name(permissionResponseList.get(0).getName() + "!?")
                                        .status(getPermissionResponseList().get(0).getStatusList().get(0))
                                        .build(),
                                PermissionRequest.builder()
                                        .id(permissionResponseList.get(1).getId())
                                        .name(permissionResponseList.get(1).getName())
                                        .status(getPermissionResponseList().get(1).getStatusList().get(0))
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
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-410"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown permission name \"" + permissionResponseList.get(0).getName() + "!?" + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("새로운 역할 추가 실패 인수 테스트 - 잘못된 permission_status 요청")
    public void CREATE_NEW_CHARACTER_FAIL_INVALID_PERMISSION_STATUS_ACCEPTANCE_TEST()
            throws Exception{
        // given
        List<PermissionResponse> permissionResponseList = getPermissionResponseList();
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String characterName = "new Character";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(
                        List.of(
                                PermissionRequest.builder()
                                        .id(permissionResponseList.get(0).getId())
                                        .name(permissionResponseList.get(0).getName())
                                        .status(getPermissionResponseList().get(0).getStatusList().get(0))
                                        .build(),
                                PermissionRequest.builder()
                                        .id(permissionResponseList.get(1).getId())
                                        .name(permissionResponseList.get(1).getName())
                                        .status("Go lang")
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
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-411"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown permission id \"" + permissionResponseList.get(1).getId()
                        + "\" name \"" + permissionResponseList.get(1).getName() + "\" status \"Go lang\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("새로운 역할 추가 실패 인수 테스트 - 중복된 character_name 추가 요청")
    public void CREATE_NEW_CHARACTER_FAIL_DUPLICATED_CHARACTER_NAME_ACCEPTANCE_TEST()
            throws Exception{
        // given
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
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
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-412"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Duplicated character name detected \"duplicate\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("역할 목록 조회 성공 인수 테스트")
    public void INQUIRY_CHARACTER_LIST_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String characterName = "mock acceptance";
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
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.characters.[0].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.characters.[0].character_name").value("mock acceptance"),
                MockMvcResultMatchers.jsonPath("$.characters.[1].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.characters.[1].character_name").value("Guest"),
                MockMvcResultMatchers.jsonPath("$.characters.[2].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.characters.[2].character_name").value("Admin")
        );
    }

    @Test
    @DisplayName("역할 목록 조회 실패 인수 테스트 - 최고 관리자가 아님")
    public void INQUIRY_CHARACTER_LIST_FAIL_NOT_ADMIN_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("helloow", "hello world", "1234abcd!");

        // when
        ResultActions result = PermissionAcceptanceTestHelper.inquiryCharacterList(mvc, token);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 역할 조회 성공 인수 테스트")
    public void INQUIRY_CHARACTER_BY_NAME_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String characterName = "something new";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of())
                .build();

        // when
        PermissionAcceptanceTestHelper.createCharacter(mvc, token, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(characterName);

        ResultActions result = PermissionAcceptanceTestHelper.inquirySpecificCharacter(mvc, characterName, token);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.character_name").value(characterName),
                MockMvcResultMatchers.jsonPath("$.permissions.[0].permission_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.permissions.[0].permission_name").isString(),
                MockMvcResultMatchers.jsonPath("$.permissions.[0].permission_status").isString()
        );
    }

    @Test
    @DisplayName("특정 역할 조회 실패 인수테스트 - 최고 관리자가 아님")
    public void INQUIRY_CHARACTER_BY_NAME_FAIL_NOT_ADMIN_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("helloow", "hello world", "1ABV234!");
        String characterName = "Character manager";

        // when
        ResultActions result = PermissionAcceptanceTestHelper
                .inquirySpecificCharacter(mvc, characterName, token);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 역할 조회 실패 인수테스트 - 잘못된 character-name 으로 조회")
    public void INQUIRY_CHARACTER_BY_NAME_FAIL_WRONG_CHARACTER_NAME_ACCEPTANCE_TEST()
            throws Exception{
        // given
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
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
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-420"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Can not find character named \"unknown_character_name\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 역할 수정 성공 인수테스트")
    public void MODIFY_CHARACTER_BY_NAME_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        List<PermissionResponse> permissionResponseList = getPermissionResponseList();
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String characterName = "mock_character_name";
        String permissionName = "Character manager";
        String permissionStatus = "true";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionRequest.builder()
                                .id(permissionResponseList.get(0).getId())
                                .name(permissionResponseList.get(0).getName())
                                .status(permissionResponseList.get(0).getStatusList().get(0))
                                .build()
                )).build();

        CharacterRequest modifiedRequest = CharacterRequest.builder()
                .characterName("modified name")
                .permissionList(List.of(
                        PermissionRequest.builder()
                                .id(permissionResponseList.get(0).getId())
                                .name(permissionResponseList.get(0).getName())
                                .status(permissionResponseList.get(0).getStatusList().get(1))
                                .build()
                )).build();

        // when
        PermissionAcceptanceTestHelper.createCharacter(mvc, token, objectMapper.writeValueAsString(request)).andDo(MockMvcResultHandlers.print());

        ResultActions result = PermissionAcceptanceTestHelper
                .modifySpecificCharacter(mvc, characterName, token, objectMapper.writeValueAsString(modifiedRequest));
        deleteWaitCharacterList.add("modified name");

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
        String token = createUserAndGetToken("hello", "hello world", "1234abc!");
        String characterName = "Character manager";
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
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 역할 수정 실패 인수테스트 - 수정할 이름 중복")
    public void MODIFY_CHARACTER_BY_NAME_FAIL_DUPLICATED_NAME_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
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
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-412"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Duplicated character name detected \"duplicated_character\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 역할 수정 실패 인수테스트 - path-parameter 에 잘못된 character-name 으로 수정 요청")
    public void MODIFY_CHARACTER_BY_NAME_FAIL_INVALID_PATH_NAME_ACCEPTANCE_TEST() throws Exception{
        // given
        String characterName = "hello world character name";
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
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
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-420"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Can not find character named \"hello world character name\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 역할 수정 실패 인수테스트 - 없는 permission_name 에 대해서 요청")
    public void MODIFY_CHARACTER_BY_NAME_FAIL_INVALID_PERMISSION_NAME_ACCEPTANCE_TEST()
            throws Exception{
        // given
        List<PermissionResponse> permissionResponseList = getPermissionResponseList();
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String characterName = "mock_character_name";
        String permissionName = "Character manager";
        String permissionStatus = "true";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionRequest.builder()
                                .id(permissionResponseList.get(0).getId())
                                .name(permissionName)
                                .status(permissionStatus)
                                .build()
                )).build();
        CharacterRequest changeRequest = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionRequest.builder()
                                .id(permissionResponseList.get(0).getId())
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
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown permission name \"invalid_permission_name\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 역할 수정 실패 인수테스트 - 잘못된 permission_status 로 수정 요청")
    public void MODIFY_CHARACTER_BY_NAME_FAIL_INVALID_PERMISSION_STATUS_ACCEPTANCE_TEST()
            throws Exception{
        // given
        List<PermissionResponse> permissionResponseList = getPermissionResponseList();
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String characterName = "mock_character_name";
        String permissionName = "Character manager";
        String permissionStatus = "true";
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionRequest.builder()
                                .id(permissionResponseList.get(0).getId())
                                .name(permissionResponseList.get(0).getName())
                                .status(permissionResponseList.get(0).getStatusList().get(0))
                                .build()
                )).build();
        CharacterRequest changeRequest = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of(
                        PermissionRequest.builder()
                                .id(permissionResponseList.get(0).getId())
                                .name(permissionResponseList.get(0).getName())
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
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-411"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown permission id \"" + permissionResponseList.get(0).getId()
                        + "\" name \"" + permissionResponseList.get(0).getName() + "\" status \"wrong_status\""),
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
                MockMvcResultMatchers.jsonPath("$.permissions").isArray(),
                MockMvcResultMatchers.jsonPath("$.permissions.[0].permission_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.permissions.[0].permission_name").isString(),
                MockMvcResultMatchers.jsonPath("$.permissions.[0].permission_info").isString(),
                MockMvcResultMatchers.jsonPath("$.permissions.[0].permission_status").isArray()
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("선택 가능한 permission 목록 조회 실패 인수 테스트 - 최고 관리자 아님")
    public void INQUIRY_PERMISSION_LIST_FAIL_NOT_ADMIN_ACCEPTANCE_TEST() throws Exception{
        // given
        String url = "/permission";
        String token = createUserAndGetToken("hello", "hello world", "abc1234!");

        // when
        ResultActions result = PermissionAcceptanceTestHelper.inquiryPermissionList(mvc, token);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("역할 삭제 성공 인수 테스트")
    public void DELETE_CHARACTER_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String characterName = "mock_character";
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
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
                MockMvcResultMatchers.header().string("Api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("역할 삭제 실패 - 최고 관리자가 아님")
    public void DELETE_CHARACTER_FAIL_NOT_ADMIN_TEST() throws Exception{
        // given
        String characterName = "mock_character";
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String notAdminToken = createUserAndGetToken("hello", "hello world", "abc!1234");
        CharacterRequest request = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of()).build();

        // when
        PermissionAcceptanceTestHelper.createCharacter(mvc, token, objectMapper.writeValueAsString(request));
        deleteWaitCharacterList.add(characterName);

        ResultActions result = PermissionAcceptanceTestHelper
                .deleteSpecificCharacter(mvc, characterName, notAdminToken);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
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
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        // when
        ResultActions result = PermissionAcceptanceTestHelper
                .deleteSpecificCharacter(mvc, characterName, token);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-420"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Can not find character named \"mock_character\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("역할 생성 실패 테스트 - 역할 이름에 공백이 들어옴")
    public void CREATE_CHARACTER_FAIL_BLANK_CHARACTER_NAME_TEST() throws Exception{
        // given
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        CharacterRequest characterRequest = CharacterRequest.builder()
                .characterName(" ")
                .permissionList(List.of())
                .build();

        // when
        ResultActions resultActions = PermissionAcceptanceTestHelper.createCharacter(mvc, token, objectMapper.writeValueAsString(characterRequest));

        // then
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string("Api-version", "1.0"),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-413")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("새로운 역할 생성 및 조회 성공 테스트 - permission에 공백")
    public void CREATE_CHARACTER_SUCCESS_TEST_BLANK_PERMISSION() throws Exception{
        // given
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String characterName = "hello world";
        CharacterRequest characterRequest = CharacterRequest.builder()
                .characterName(characterName)
                .permissionList(List.of())
                .build();

        // when
        PermissionAcceptanceTestHelper.createCharacter(mvc, token, objectMapper.writeValueAsString(characterRequest));
        deleteWaitCharacterList.add(characterName);
        ResultActions resultActions = PermissionAcceptanceTestHelper.inquirySpecificCharacter(mvc, characterName, token);

        // then
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", "1.0"),
                MockMvcResultMatchers.jsonPath("$.character_name").value(characterName)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("역할 수정 실패 테스트 - Guest 와 Admin 역할 수정 불가")
    public void UPDATE_CHARACTER_FAIL_TEST_TRY_MODIFY_GUEST_AND_ADMIN_CHARACTER() throws Exception{
        // given
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String characterName = "Guest";
        CharacterRequest characterChangeRequest = CharacterRequest.builder()
                .characterName("Hello world")
                .permissionList(List.of())
                .build();

        // when
        ResultActions result = PermissionAcceptanceTestHelper
                .modifySpecificCharacter(mvc, characterName, token, objectMapper.writeValueAsString(characterChangeRequest));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    private String createUserAndGetToken(String name, String userId, String userPassword) throws Exception{
        UserRequest userRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber("010-1234-1234")
                .build();
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest)).andDo(MockMvcResultHandlers.print());
        userCreateRequestList.add(userRequest);

        return AuthenticationAcceptanceTestHelper.getToken(mvc, objectMapper, AuthTokenRequest.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build());
    }

    private List<PermissionResponse> getPermissionResponseList() throws Exception{
        String token = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String content = PermissionAcceptanceTestHelper.inquiryPermissionList(mvc, token).andReturn().getResponse().getContentAsString();
        Map<String, List<PermissionResponse>> permissionResponseMap = objectMapper.readValue(content, new TypeReference<>(){});
        return permissionResponseMap.get("permissions");
    }

}
