package org.waldreg.acceptance.reward;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
import org.waldreg.controller.reward.tag.request.RewardTagRequest;
import org.waldreg.controller.reward.tag.response.RewardTagResponse;
import org.waldreg.controller.reward.users.response.RewardTagWrapperResponse;
import org.waldreg.controller.reward.users.response.UsersRewardTagResponse;
import org.waldreg.controller.user.request.UserRequest;
import org.waldreg.controller.user.response.UserResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class RewardAcceptanceTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String apiVersion = "1.0";

    private final ArrayList<UserRequest> userCreateRequestList = new ArrayList<>();

    private final ArrayList<RewardTagResponse> rewardTagResponseList = new ArrayList<>();

    @BeforeEach
    @AfterEach
    public void INITIATE_USER() throws Exception{
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

    @BeforeEach
    @AfterEach
    public void INITIATE_REWARD_TAG() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        ArrayList<RewardTagResponse> rewardTagResponseList = new ArrayList<>(
                getRewardTagResponseMap(RewardAcceptanceTestHelper.inquiryRewardTagList(mvc, adminToken)).get("reward_tags")
        );
        for(RewardTagResponse rewardTagResponse : rewardTagResponseList){
            RewardAcceptanceTestHelper.deleteRewardTag(mvc, adminToken, rewardTagResponse.getRewardTagId());
            RewardAcceptanceTestHelper.deleteRewardTag(mvc, adminToken, rewardTagResponse.getRewardTagId())
                    .andExpectAll(
                            MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.header().string("Api-version", apiVersion),
                            MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                            MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                            MockMvcResultMatchers.jsonPath("$.messages").value("Unknown reward tag id"),
                            MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
                    );
        }
        rewardTagResponseList.clear();
    }

    @Test
    @DisplayName("상, 벌점 사유 태그 생성 성공 인수 테스트")
    public void CREATE_REWARD_TAG_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String rewardTagTitle = "contest winner";
        int rewardPoint = 10;
        RewardTagRequest rewardTagRequest = RewardTagRequest.builder()
                .rewardTagTitle(rewardTagTitle)
                .rewardPoint(rewardPoint)
                .build();

        // when
        ResultActions result = RewardAcceptanceTestHelper.createRewardTag(mvc, adminToken, objectMapper.writeValueAsString(rewardTagRequest));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("상, 벌점 사유 태그 생성 실패 인수 테스트 - 상점을 생성할 권한이 없는 유저가 상점을 생성하려고 시도 한 경우")
    public void CREATE_REWARD_TAG_FAIL_NO_PERMISSION_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("some-one-else", "abc123", "aadsfc123!!");
        String rewardTagTitle = "contest winner";
        int rewardPoint = 10;
        RewardTagRequest rewardTagRequest = RewardTagRequest.builder()
                .rewardTagTitle(rewardTagTitle)
                .rewardPoint(rewardPoint)
                .build();

        // when
        ResultActions result = RewardAcceptanceTestHelper.createRewardTag(mvc, token, objectMapper.writeValueAsString(rewardTagRequest));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("상, 벌점 사유 태그 생성 실패 인수 테스트 - 인증 실패")
    public void CREATE_REWARD_TAG_AUTHENTICATE_FAIL_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = "abc.abc.abc";
        String rewardTagTitle = "contest winner";
        int rewardPoint = 10;
        RewardTagRequest rewardTagRequest = RewardTagRequest.builder()
                .rewardTagTitle(rewardTagTitle)
                .rewardPoint(rewardPoint)
                .build();

        // when
        ResultActions result = RewardAcceptanceTestHelper.createRewardTag(mvc, token, objectMapper.writeValueAsString(rewardTagRequest));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Authenticate fail"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("상, 벌점 사유 태그 조회 성공 인수 테스트")
    public void INQUIRY_REWARD_TAG_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String rewardTagTitle = "contest winner";
        int rewardPoint = 10;
        RewardTagRequest rewardTagRequest = RewardTagRequest.builder()
                .rewardTagTitle(rewardTagTitle)
                .rewardPoint(rewardPoint)
                .build();

        // when
        ResultActions resultHelloTag = RewardAcceptanceTestHelper.createRewardTag(mvc, adminToken, objectMapper.writeValueAsString(rewardTagRequest));
        ResultActions resultByeTag = RewardAcceptanceTestHelper.createRewardTag(mvc, adminToken, objectMapper.writeValueAsString(rewardTagRequest));
        ResultActions result = RewardAcceptanceTestHelper.inquiryRewardTagList(mvc, adminToken);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string("Content-type", "application/json"),
                MockMvcResultMatchers.jsonPath("$.reward_tags.[0].reward_tag_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.reward_tags.[0].reward_tag_title").value(rewardTagTitle),
                MockMvcResultMatchers.jsonPath("$.reward_tags.[0].reward_point").value(rewardPoint),
                MockMvcResultMatchers.jsonPath("$.reward_tags.[1].reward_tag_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.reward_tags.[1].reward_tag_title").value(rewardTagTitle),
                MockMvcResultMatchers.jsonPath("$.reward_tags.[1].reward_point").value(rewardPoint)
        );
    }

    @Test
    @DisplayName("상, 벌점 사유 태그 조회 실패 인수 테스트 - 조회 권한이 없는 유저가 조회를 시도한 경우")
    public void INQUIRY_REWARD_TAG_FAIL_NO_PERMISSION_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("some-one-else", "abc123", "aadsfc123!!");

        // when
        ResultActions result = RewardAcceptanceTestHelper.inquiryRewardTagList(mvc, token);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("상, 벌점 사유 태그 조회 실패 인수 테스트 - 인증 실패")
    public void INQUIRY_REWARD_TAG_FAIL_AUTHORIZATION_FAIL_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = "abc.def.ghi";

        // when
        ResultActions result = RewardAcceptanceTestHelper.inquiryRewardTagList(mvc, token);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Authenticate fail"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("상, 벌점 사유 태그 수정 성공 인수테스트")
    public void UPDATE_REWARD_TAG_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String rewardTagTitle = "contest winner";
        int rewardPoint = 10;
        RewardTagRequest rewardTagCreateRequest = RewardTagRequest.builder()
                .rewardTagTitle(rewardTagTitle)
                .rewardPoint(rewardPoint)
                .build();

        String updateTagTitle = "contest loser";
        int updatePoint = -10;
        RewardTagRequest rewardTagUpdateRequest = RewardTagRequest.builder()
                .rewardTagTitle(updateTagTitle)
                .rewardPoint(updatePoint)
                .build();


        // when
        RewardAcceptanceTestHelper.createRewardTag(mvc, adminToken, objectMapper.writeValueAsString(rewardTagCreateRequest));
        ResultActions rewardTagResult = RewardAcceptanceTestHelper.inquiryRewardTagList(mvc, adminToken);
        Map<String, List<RewardTagResponse>> rewardTagResponseMap = getRewardTagResponseMap(rewardTagResult);
        List<RewardTagResponse> rewardTagResponseList = rewardTagResponseMap.get("reward_tags");
        int rewardTagId = rewardTagResponseList.get(0).getRewardTagId();

        RewardAcceptanceTestHelper.updateRewardTag(mvc, adminToken, rewardTagId, objectMapper.writeValueAsString(rewardTagUpdateRequest));
        rewardTagResult = RewardAcceptanceTestHelper.inquiryRewardTagList(mvc, adminToken);
        List<RewardTagResponse> result = getRewardTagResponseMap(rewardTagResult).get("reward_tags");

        // then
        Assertions.assertAll(
                ()-> Assertions.assertEquals(result.get(0).getRewardTagTitle(), updateTagTitle),
                ()-> Assertions.assertEquals(result.get(0).getRewardPoint(), updatePoint)
        );
    }

    @Test
    @DisplayName("상, 벌점 사유 태그 수정 실패 테스트 - 권한 없음")
    public void UPDATE_REWARD_TAG_FAIL_NO_PERMISSION_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("some-one-else", "abc123", "aadsfc123!!");
        RewardTagRequest updateRequest = RewardTagRequest.builder()
                .rewardTagTitle("hello world")
                .rewardPoint(-100)
                .build();

        // when
        ResultActions result = RewardAcceptanceTestHelper.updateRewardTag(mvc, token, 1, objectMapper.writeValueAsString(updateRequest));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("상, 벌점 사유 태그 수정 실패 테스트 - reward-tag-id에 해당하는 상, 벌점 태그를 찾을 수 없음")
    public void UPDATE_REWARD_TAG_FAIL_CANNOT_FIND_REWARD_TAG_ID_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        RewardTagRequest updateRequest = RewardTagRequest.builder()
                .rewardTagTitle("hello world")
                .rewardPoint(-100)
                .build();

        // when
        ResultActions result = RewardAcceptanceTestHelper.updateRewardTag(mvc, adminToken, 999, objectMapper.writeValueAsString(updateRequest));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown reward tag id"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("상, 벌점 사유 태그 수정 실패 테스트 - 인증 실패")
    public void UPDATE_REWARD_TAG_FAIL_AUTHENTICATE_FAIL_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = "abc.abc.abc";
        RewardTagRequest updateRequest = RewardTagRequest.builder()
                .rewardTagTitle("hello world")
                .rewardPoint(-100)
                .build();

        // when
        ResultActions result = RewardAcceptanceTestHelper.updateRewardTag(mvc, adminToken, 999, objectMapper.writeValueAsString(updateRequest));

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Authenticate fail"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("상, 벌점 태그 삭제 성공 테스트")
    public void DELETE_REWARD_TAG_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        RewardTagRequest createRequest = RewardTagRequest.builder()
                .rewardTagTitle("hello world")
                .rewardPoint(-100)
                .build();

        // when
        RewardAcceptanceTestHelper.createRewardTag(mvc, adminToken, objectMapper.writeValueAsString(createRequest));
        ResultActions inquiryTagResult = RewardAcceptanceTestHelper.inquiryRewardTagList(mvc, adminToken);
        RewardTagResponse rewardTagResponse = getRewardTagResponseMap(inquiryTagResult).get("reward_tags").get(0);
        RewardAcceptanceTestHelper.deleteRewardTag(mvc, adminToken, rewardTagResponse.getRewardTagId());

        ResultActions result =  RewardAcceptanceTestHelper.deleteRewardTag(mvc, adminToken, rewardTagResponse.getRewardTagId());

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown reward tag id"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("상, 벌점 태그 삭제 실패 인수 테스트 - 권한 없음")
    public void DELETE_REWARD_TAG_FAIL_NO_PERMISSION_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("some-one-else", "abc123", "aadsfc123!!");

        // when
        ResultActions result =  RewardAcceptanceTestHelper.deleteRewardTag(mvc, token, 1);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("유저에게 상, 벌점 부여 성공 인수테스트")
    public void GIVE_REWARD_TAG_TO_USER_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        RewardTagRequest createRequest = RewardTagRequest.builder()
                .rewardTagTitle("contest loser")
                .rewardPoint(-100)
                .build();

        String name = "someone";
        String userId = "some-one";
        String userPassword = "adcgd123!!@@";
        UserRequest userRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber("010-1234-1234")
                .build();

        // when
        RewardAcceptanceTestHelper.createRewardTag(mvc, adminToken, objectMapper.writeValueAsString(createRequest));
        ResultActions inquiryTagResult = RewardAcceptanceTestHelper.inquiryRewardTagList(mvc, adminToken);
        RewardTagResponse rewardTagResponse = getRewardTagResponseMap(inquiryTagResult).get("reward_tags").get(0);
        int rewardTagId = rewardTagResponse.getRewardTagId();

        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest)).andDo(MockMvcResultHandlers.print());
        userCreateRequestList.add(userRequest);

        int id = getUserResponse(userId, adminToken).getId();
        ResultActions result = RewardAcceptanceTestHelper.givenRewardTagToUser(mvc, adminToken, ""+id, rewardTagId);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("유저에게 상, 벌점 부여 실패 인수테스트 - 권한 없음")
    public void GIVEN_REWARD_TAG_TO_USER_FAIL_NO_PERMISSION_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("some", "abc1234", "aafadsfc123!!");

        // when
        ResultActions result = RewardAcceptanceTestHelper.givenRewardTagToUser(mvc, token, ""+1, 1);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("유저에게 상, 벌점 부여 실패 인수테스트 - 태그의 id를 찾을 수 없음")
    public void GIVEN_REWARD_TAG_TO_USER_FAIL_CANNOT_FIND_REWARD_TAG_ID_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String name = "someone";
        String userId = "some-one";
        String userPassword = "adcgd123!!@@";
        UserRequest userRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber("010-1234-1234")
                .build();

        // when
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest)).andDo(MockMvcResultHandlers.print());
        userCreateRequestList.add(userRequest);

        int id = getUserResponse(userId, adminToken).getId();
        ResultActions result = RewardAcceptanceTestHelper.givenRewardTagToUser(mvc, adminToken, ""+id, 100);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown reward tag id"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("유저에게 상, 벌점 부여 실패 인수테스트 - 인증 실패")
    public void GIVEN_REWARD_TAG_TO_USER_FAIL_AUTHENTICATE_FAIL_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = "abc.abc.abc";

        // when
        ResultActions result = RewardAcceptanceTestHelper.givenRewardTagToUser(mvc, token, ""+1, 100);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Authenticate fail"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("유저에게 상, 벌점 부여 실패 인수테스트 - 유저를 찾을 수 없음")
    public void GIVEN_REWARD_TAG_TO_USER_FAIL_CANNOT_FIND_USER_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        RewardTagRequest createRequest = RewardTagRequest.builder()
                .rewardTagTitle("contest loser")
                .rewardPoint(-100)
                .build();
        // when
        RewardAcceptanceTestHelper.createRewardTag(mvc, adminToken, objectMapper.writeValueAsString(createRequest));
        ResultActions inquiryTagResult = RewardAcceptanceTestHelper.inquiryRewardTagList(mvc, adminToken);
        RewardTagResponse rewardTagResponse = getRewardTagResponseMap(inquiryTagResult).get("reward_tags").get(0);
        int rewardTagId = rewardTagResponse.getRewardTagId();

        ResultActions result = RewardAcceptanceTestHelper.givenRewardTagToUser(mvc, adminToken, ""+1, rewardTagId);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown user id"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("모든 유저의 상, 벌점 초기화 성공 인수테스트")
    public void RESET_REWARD_TAG_FROM_ALL_USER_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        // when
        ResultActions result = RewardAcceptanceTestHelper.resetRewardTagFromAllUsers(mvc, adminToken);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", "1.0")
        );
    }

    @Test
    @DisplayName("모든 유저 상, 벌점 초기화 실패 인수테스트 - 권한 없음")
    public void RESET_REWARD_TAG_FROM_ALL_USER_FAIL_NO_PERMISSION_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("some", "abc1234", "aafadsfc123!!");

        // when
        ResultActions result = RewardAcceptanceTestHelper.resetRewardTagFromAllUsers(mvc, token);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 유저의 상, 벌점 조회 성공 인수테스트")
    public void INQUIRY_SPECIFY_USERS_REWARD_TAG_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        RewardTagRequest createRequest = RewardTagRequest.builder()
                .rewardTagTitle("contest loser")
                .rewardPoint(-100)
                .build();

        String name = "hello world";
        String userId = "hello-world";
        String userPassword = "adcgd123!!@@";
        UserRequest userRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber("010-1234-1234")
                .build();

        // when
        RewardAcceptanceTestHelper.createRewardTag(mvc, adminToken, objectMapper.writeValueAsString(createRequest));
        ResultActions inquiryTagResult = RewardAcceptanceTestHelper.inquiryRewardTagList(mvc, adminToken);
        RewardTagResponse rewardTagResponse = getRewardTagResponseMap(inquiryTagResult).get("reward_tags").get(0);
        int rewardTagId = rewardTagResponse.getRewardTagId();

        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest)).andDo(MockMvcResultHandlers.print());
        userCreateRequestList.add(userRequest);

        int id = getUserResponse(userId, adminToken).getId();
        RewardAcceptanceTestHelper.givenRewardTagToUser(mvc, adminToken, ""+id, rewardTagId);
        RewardAcceptanceTestHelper.givenRewardTagToUser(mvc, adminToken, ""+id, rewardTagId);
        ResultActions resultActions = RewardAcceptanceTestHelper.inquirySpecifyUsersRewardTags(mvc, adminToken, id);

        // then
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.id").value(id),
                MockMvcResultMatchers.jsonPath("$.name").value(userRequest.getName()),
                MockMvcResultMatchers.jsonPath("$.user_id").value(userRequest.getUserId()),
                MockMvcResultMatchers.jsonPath("$.reward").value(-200),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[0].reward_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[0].reward_tag_id").value(rewardTagId),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[0].reward_tag_title").value(rewardTagResponse.getRewardTagTitle()),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[0].reward_presented_at").isString(),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[0].reward_point").value(-100),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[1].reward_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[1].reward_tag_id").value(rewardTagId),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[1].reward_tag_title").value(rewardTagResponse.getRewardTagTitle()),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[1].reward_presented_at").isString(),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[1].reward_point").value(-100)
        );
    }

    @Test
    @DisplayName("특정 유저의 상 벌점 조회 성공 테스트 - 권한은 없지만 자기자신의 상 벌점을 조회하려고 함")
    public void INQUIRY_SPECIFY_USERS_REWARD_TAG_FAIL_NO_PERMISSION_BUT_MYSELF_SUCCESS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        RewardTagRequest createRequest = RewardTagRequest.builder()
                .rewardTagTitle("contest loser")
                .rewardPoint(-100)
                .build();

        String name = "hello world";
        String userId = "hello-world";
        String userPassword = "adcgd123!!@@";
        UserRequest userRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber("010-1234-1234")
                .build();

        // when
        RewardAcceptanceTestHelper.createRewardTag(mvc, adminToken, objectMapper.writeValueAsString(createRequest));
        ResultActions inquiryTagResult = RewardAcceptanceTestHelper.inquiryRewardTagList(mvc, adminToken);
        RewardTagResponse rewardTagResponse = getRewardTagResponseMap(inquiryTagResult).get("reward_tags").get(0);
        int rewardTagId = rewardTagResponse.getRewardTagId();

        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest)).andDo(MockMvcResultHandlers.print());
        userCreateRequestList.add(userRequest);

        int id = getUserResponse(userId, adminToken).getId();
        RewardAcceptanceTestHelper.givenRewardTagToUser(mvc, adminToken, ""+id, rewardTagId);
        RewardAcceptanceTestHelper.givenRewardTagToUser(mvc, adminToken, ""+id, rewardTagId);
        String token = AuthenticationAcceptanceTestHelper.getToken(mvc, objectMapper, AuthTokenRequest.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build());
        ResultActions resultActions = RewardAcceptanceTestHelper.inquirySpecifyUsersRewardTags(mvc, token, id);

        // then
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.id").value(id),
                MockMvcResultMatchers.jsonPath("$.name").value(userRequest.getName()),
                MockMvcResultMatchers.jsonPath("$.user_id").value(userRequest.getUserId()),
                MockMvcResultMatchers.jsonPath("$.reward").value(-200),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[0].reward_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[0].reward_tag_id").value(rewardTagId),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[0].reward_tag_title").value(rewardTagResponse.getRewardTagTitle()),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[0].reward_presented_at").isString(),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[0].reward_point").value(-100),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[1].reward_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[1].reward_tag_id").value(rewardTagId),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[1].reward_tag_title").value(rewardTagResponse.getRewardTagTitle()),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[1].reward_presented_at").isString(),
                MockMvcResultMatchers.jsonPath("$.reward_infos.[1].reward_point").value(-100)
        );
    }

    @Test
    @DisplayName("특정 유저의 상 벌점 조회 실패 테스트 - 권한 없음")
    public void INQUIRY_SPECIFY_USERS_REWARD_TAG_FAIL_NO_PERMISSION_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("some", "abc1234", "aafadsfc123!!");

        // when
        ResultActions result = RewardAcceptanceTestHelper.inquirySpecifyUsersRewardTags(mvc, token, 1);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 유저의 상 벌점 조회 실패 테스트 - 유저를 찾을 수 없음")
    public void INQUIRY_SPECIFY_USERS_REWARD_TAG_FAIL_CANNOT_FIND_USER_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        // when
        ResultActions resultActions = RewardAcceptanceTestHelper.inquirySpecifyUsersRewardTags(mvc, adminToken, 10);

        // then
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown user id"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 유저의 상 벌점 조회 실패 인수 테스트 - 인증 실패")
    public void INQUIRY_SPECIFY_USERS_REWARD_TAG_FAIL_AUTHENTICATE_FAIL_USER_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = "abc.def.ghi";

        // when
        ResultActions result = RewardAcceptanceTestHelper.inquirySpecifyUsersRewardTags(mvc, token, 10);

        // then
        result.andExpectAll(
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Authenticate fail"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 유저에게 상, 벌점 태그 삭제 성공 인수 테스트")
    public void DELETE_SPECIFY_USERS_REWARD_TAG_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        RewardTagRequest createRequest = RewardTagRequest.builder()
                .rewardTagTitle("contest loser")
                .rewardPoint(-100)
                .build();

        String name = "hello world";
        String userId = "hello-world";
        String userPassword = "adcgd123!!@@";
        UserRequest userRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber("010-1234-1234")
                .build();

        // when
        RewardAcceptanceTestHelper.createRewardTag(mvc, adminToken, objectMapper.writeValueAsString(createRequest));
        ResultActions inquiryTagResult = RewardAcceptanceTestHelper.inquiryRewardTagList(mvc, adminToken);
        RewardTagResponse rewardTagResponse = getRewardTagResponseMap(inquiryTagResult).get("reward_tags").get(0);
        int rewardTagId = rewardTagResponse.getRewardTagId();

        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest)).andDo(MockMvcResultHandlers.print());
        userCreateRequestList.add(userRequest);

        int id = getUserResponse(userId, adminToken).getId();
        RewardAcceptanceTestHelper.givenRewardTagToUser(mvc, adminToken, ""+id, rewardTagId);
        ResultActions specifyUsersRewardTags = RewardAcceptanceTestHelper.inquirySpecifyUsersRewardTags(mvc, adminToken, id);
        UsersRewardTagResponse usersRewardTagResponse = getUsersRewardTagResponse(specifyUsersRewardTags);
        ResultActions resultActions = RewardAcceptanceTestHelper
                .deleteSpecifyUsersRewardTags(mvc, adminToken, id, usersRewardTagResponse.getRewardInfoList().get(0).getRewardId());

        // then
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("특정 유저에게 상, 벌점 삭제 실패 인수 테스트 - 권한 없음")
    public void DELETE_SPECIFY_USERS_REWARD_TAG_FAIL_NO_PERMISSION_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("heeeeee", "hee123", "heee7788!!@@");

        // when
        ResultActions resultActions = RewardAcceptanceTestHelper
                .deleteSpecifyUsersRewardTags(mvc, token, 1, 1);

        // then
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 유저에게 상, 벌점 삭제 실패 인수 테스트 - 유저에 속한 reward id를 찾을 수 없음")
    public void DELETE_SPECIFY_USERS_REWARD_TAG_FAIL_UNKNOWN_REWARD_ID_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        RewardTagRequest createRequest = RewardTagRequest.builder()
                .rewardTagTitle("contest loser")
                .rewardPoint(-100)
                .build();

        String name = "hello world";
        String userId = "hello-world";
        String userPassword = "adcgd123!!@@";
        UserRequest userRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber("010-1234-1234")
                .build();

        // when
        RewardAcceptanceTestHelper.createRewardTag(mvc, adminToken, objectMapper.writeValueAsString(createRequest));
        ResultActions inquiryTagResult = RewardAcceptanceTestHelper.inquiryRewardTagList(mvc, adminToken);
        RewardTagResponse rewardTagResponse = getRewardTagResponseMap(inquiryTagResult).get("reward_tags").get(0);
        int rewardTagId = rewardTagResponse.getRewardTagId();

        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest)).andDo(MockMvcResultHandlers.print());
        userCreateRequestList.add(userRequest);

        int id = getUserResponse(userId, adminToken).getId();
        RewardAcceptanceTestHelper.givenRewardTagToUser(mvc, adminToken, ""+id, rewardTagId);
        ResultActions specifyUsersRewardTags = RewardAcceptanceTestHelper.inquirySpecifyUsersRewardTags(mvc, adminToken, id);
        UsersRewardTagResponse usersRewardTagResponse = getUsersRewardTagResponse(specifyUsersRewardTags);
        ResultActions resultActions = RewardAcceptanceTestHelper
                .deleteSpecifyUsersRewardTags(mvc, adminToken, id, 1000);

        // then
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown reward id"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 유저에게 상, 벌점 삭제 실패 인수 테스트 - id에 해당하는 유저를 찾을 수 없음")
    public void DELETE_SPECIFY_USERS_REWARD_TAG_FAIL_UNKNOWN_USER_ID_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        // when
        ResultActions resultActions = RewardAcceptanceTestHelper.deleteSpecifyUsersRewardTags(mvc, adminToken, 100, 1);

        // then
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown user id"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    private UserResponse getUserResponse(String userId, String token) throws Exception{
        return objectMapper.readValue(
                UserAcceptanceTestHelper.inquiryUserWithToken(mvc,userId, token)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), UserResponse.class);
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

    private Map<String, List<RewardTagResponse>> getRewardTagResponseMap(ResultActions resultActions) throws Exception{
        String content = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, new TypeReference<>(){});
    }

    private UsersRewardTagResponse getUsersRewardTagResponse(ResultActions resultActions) throws Exception{
        String content = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, UsersRewardTagResponse.class);
    }

}
