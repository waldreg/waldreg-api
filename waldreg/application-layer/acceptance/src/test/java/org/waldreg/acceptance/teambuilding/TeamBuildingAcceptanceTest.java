package org.waldreg.acceptance.teambuilding;

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
import org.waldreg.acceptance.user.UserAcceptanceTestHelper;
import org.waldreg.auth.request.AuthTokenRequest;
import org.waldreg.controller.joiningpool.request.UserRequest;
import org.waldreg.controller.teambuilding.request.TeamBuildingRequest;
import org.waldreg.controller.teambuilding.request.TeamBuildingUpdateRequest;
import org.waldreg.controller.teambuilding.request.TeamRequest;
import org.waldreg.controller.teambuilding.request.TeamUpdateRequest;
import org.waldreg.controller.teambuilding.request.UserWeightRequest;
import org.waldreg.controller.teambuilding.response.TeamBuildingListResponse;
import org.waldreg.controller.teambuilding.response.TeamBuildingResponse;
import org.waldreg.controller.user.response.UserResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class TeamBuildingAcceptanceTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String apiVersion = "1.0";

    private final List<UserRequest> userRequestList = new ArrayList<>();

    @BeforeEach
    @AfterEach
    public void INITIATE_TEAM_BUILDING() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 30, adminToken);
        TeamBuildingListResponse teamBuildingListResponse = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        for (TeamBuildingResponse teamBuildingResponse : teamBuildingListResponse.getTeamBuildingList()){
            int id = teamBuildingResponse.getTeamBuildingId();
            TeamBuildingAcceptanceTestHelper.deleteTeamBuilding(mvc, id, adminToken);
            result = TeamBuildingAcceptanceTestHelper.inquirySpecificTeamBuilding(mvc, id, adminToken);
            result.andExpectAll(
                    MockMvcResultMatchers.status().isBadRequest(),
                    MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                    MockMvcResultMatchers.header().string("api-version", apiVersion),
                    MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                    MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-400"),
                    MockMvcResultMatchers.jsonPath("$.messages").value("Cannot find teambuilding id \"" + id + "\""),
                    MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
            );
        }
    }

    @BeforeEach
    @AfterEach
    public void INITIATE_USER() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        for (UserRequest request : userRequestList){
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
                    MockMvcResultMatchers.jsonPath("$.code").value("USER-406"),
                    MockMvcResultMatchers.jsonPath("$.messages").value("Unknown user_id \"" + request.getUserId() + "\""),
                    MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
            );
        }
        userRequestList.clear();
    }

    @Test
    @DisplayName("팀빌딩 그룹 생성 성공 테스트")
    public void CREATE_NEW_TEAM_BUILDING_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();

        //when
        ResultActions result = TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 생성 실패 테스트 - teambuilding_title overflow (1000자 이상)")
    public void CREATE_NEW_TEAM_BUILDING_FAIL_CAUSE_TITLE_OVERFLOW_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = createOverflow();
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();

        //when
        ResultActions result = TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-401"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Teambuilding title cannot be more than 1000 current length \"" + title.length() + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 생성 실패 테스트 - team_count가 0 이하일 때")
    public void CREATE_NEW_TEAM_BUILDING_FAIL_CAUSE_INVALID_TEAM_COUNT_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 0;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();

        //when
        ResultActions result = TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-402"),
                MockMvcResultMatchers.jsonPath("$.messages").value("The number of team cannot be less than or equal to zero, current team count \"" + teamCount + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 생성 실패 테스트 - teambuilding_title이 공백")
    public void CREATE_NEW_TEAM_BUILDING_FAIL_CAUSE_TEAM_BUILDING_TITLE_IS_BLANK_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();

        //when
        ResultActions result = TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Teambuilding title cannot be blank"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 생성 실패 테스트 - 잘못된 user의 weight 범위")
    public void CREATE_NEW_TEAM_BUILDING_FAIL_CAUSE_INVALID_USER_WEIGHT_RANGE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        userList.get(0).setWeight(-1);
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();

        //when
        ResultActions result = TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-404"),
                MockMvcResultMatchers.jsonPath("$.messages").value("User's weight value should be between 1 and 10, current weight \"" + userList.get(0).getWeight() + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 생성 실패 테스트 - team_count가 user의 수보다 클 때")
    public void CREATE_NEW_TEAM_BUILDING_FAIL_CAUSE_TEAM_COUNT_EXCEED_USER_COUNT_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 6;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();

        //when
        ResultActions result = TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-410"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Team count \"" + teamCount + "\" cannot exceed member count \"" + userList.size() + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 생성 실패 테스트 - 권한 없음")
    public void CREATE_NEW_TEAM_BUILDING_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String wrongToken = createUserAndGetToken("lin","linirini","2fhfhfhfh!");
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();

        //when
        ResultActions result = TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), wrongToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 전체 조회 성공 테스트")
    public void INQUIRY_ALL_TEAM_BUILDING_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        String title2 = "3nd Algorithm Contest Team";
        int teamCount = 2;
        int teamCount2 = 3;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        List<UserWeightRequest> userList2 = createUserWeightRequestList2();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        TeamBuildingRequest teamBuildingRequest2 = TeamBuildingRequest.builder()
                .teamBuildingTitle(title2)
                .teamCount(teamCount2)
                .userList(userList2)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest2), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(2),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[0].teambuilding_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[0].teambuilding_title").value(teamBuildingRequest2.getTeamBuildingTitle()),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[0].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[0].teams.[0].team_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[0].teams.[0].team_name").isString(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[0].teams.[0].last_modified_at").isString(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[0].teams.[0].team_members.[0].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[0].teams.[0].team_members.[0].user_id").isString(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[0].teams.[0].team_members.[0].name").isString(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[0].teams.[1].team_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[0].teams.[1].team_members.[0]").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[1].teambuilding_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[1].teambuilding_title").value(teamBuildingRequest.getTeamBuildingTitle()),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[1].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[1].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[1].teams.[0].team_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[1].teams.[0].team_name").isString(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[1].teams.[0].last_modified_at").isString(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[1].teams.[0].team_members.[0].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[1].teams.[0].team_members.[0].user_id").isString(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[1].teams.[0].team_members.[0].name").isString(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[1].teams.[1].team_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[1].teams.[1].team_members.[0]").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[1].teams.[1].team_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[1].teams.[1].team_members.[0]").isNotEmpty()
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("팀빌딩 그룹 전체 조회 실패 테스트 - 잘못된 범위")
    public void INQUIRY_ALL_TEAM_BUILDING_FAIL_CAUSE_INVALID_RANGE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        String title2 = "3rd Algorithm Contest Team";
        int teamCount = 2;
        int teamCount2 = 3;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        List<UserWeightRequest> userList2 = createUserWeightRequestList2();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        TeamBuildingRequest teamBuildingRequest2 = TeamBuildingRequest.builder()
                .teamBuildingTitle(title2)
                .teamCount(teamCount2)
                .userList(userList2)
                .build();
        int startIdx = 0;
        int endIdx = -2;

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest2), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, startIdx, endIdx, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-405"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid range start-idx \"" + startIdx + "\", end-idx \"" + endIdx + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("팀빌딩 그룹 조회 성공 테스트")
    public void INQUIRY_SPECIFIC_TEAM_BUILDING_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        String title2 = "3rd Algorithm Contest Team";
        int teamCount = 2;
        int teamCount2 = 3;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        List<UserWeightRequest> userList2 = createUserWeightRequestList2();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        TeamBuildingRequest teamBuildingRequest2 = TeamBuildingRequest.builder()
                .teamBuildingTitle(title2)
                .teamCount(teamCount2)
                .userList(userList2)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest2), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int id = teamBuildingList.getTeamBuildingList().get(0).getTeamBuildingId();
        result = TeamBuildingAcceptanceTestHelper.inquirySpecificTeamBuilding(mvc, id, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.teambuilding_id").value(id),
                MockMvcResultMatchers.jsonPath("$.teambuilding_title").value(teamBuildingRequest2.getTeamBuildingTitle()),
                MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.teams.[0].team_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teams.[0].last_modified_at").isString(),
                MockMvcResultMatchers.jsonPath("$.teams.[0].team_name").isString(),
                MockMvcResultMatchers.jsonPath("$.teams.[0].team_members.[0].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teams.[0].team_members.[0].user_id").isString(),
                MockMvcResultMatchers.jsonPath("$.teams.[0].team_members.[0].name").isString(),
                MockMvcResultMatchers.jsonPath("$.teams.[1].team_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teams.[1].team_members.[0]").isNotEmpty()
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("팀빌딩 그룹 조회 실패 테스트 - 없는 teambuilding id")
    public void INQUIRY_SPECIFIC_TEAM_BUILDING_FAIL_CAUSE_UNKNOWN_TEAM_BUILDING_ID_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        String title2 = "3rd Algorithm Contest Team";
        int teamCount = 2;
        int teamCount2 = 3;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        List<UserWeightRequest> userList2 = createUserWeightRequestList2();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        TeamBuildingRequest teamBuildingRequest2 = TeamBuildingRequest.builder()
                .teamBuildingTitle(title2)
                .teamCount(teamCount2)
                .userList(userList2)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest2), adminToken);
        int id = 0;
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquirySpecificTeamBuilding(mvc, id, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-400"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Cannot find teambuilding id \"" + id + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 추가 성공 테스트")
    public void ADD_NEW_TEAM_IN_TEAM_BUILDING_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        List<String> memberList = new ArrayList<>();
        TeamRequest teamRequest = TeamRequest.builder()
                .teamName("chisami")
                .members(memberList)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int id = teamBuildingList.getTeamBuildingList().get(0).getTeamBuildingId();
        result = TeamBuildingAcceptanceTestHelper.addNewTeam(mvc, id, objectMapper.writeValueAsString(teamRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 추가 실패 테스트 - teambuilding_id가 없을 때")
    public void ADD_NEW_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_INVALID_TEAM_BUILDING_ID_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        List<String> memberList = new ArrayList<>();
        TeamRequest teamRequest = TeamRequest.builder()
                .teamName("chisami")
                .members(memberList)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        int id = 0;
        ResultActions result = TeamBuildingAcceptanceTestHelper.addNewTeam(mvc, id, objectMapper.writeValueAsString(teamRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-400"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Cannot find teambuilding id \"" + id + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 추가 실패 테스트 - team_name 중복")
    public void ADD_NEW_TEAM_IN_TEAM_BUILDING_CAUSE_DUPLICATED_TEAM_NAME_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        List<String> memberList = new ArrayList<>();
        TeamRequest teamRequest = TeamRequest.builder()
                .teamName("chisami")
                .members(memberList)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int id = teamBuildingList.getTeamBuildingList().get(0).getTeamBuildingId();
        TeamBuildingAcceptanceTestHelper.addNewTeam(mvc, id, objectMapper.writeValueAsString(teamRequest), adminToken);
        result = TeamBuildingAcceptanceTestHelper.addNewTeam(mvc, id, objectMapper.writeValueAsString(teamRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-406"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Duplicated team name \"" + teamRequest.getTeamName() + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 추가 실패 테스트 - team_name이 공백")
    public void ADD_NEW_TEAM_IN_TEAM_BUILDING_CAUSE_TEAM_NAME_IS_BLANK_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        List<String> memberList = new ArrayList<>();
        TeamRequest teamRequest = TeamRequest.builder()
                .teamName("")
                .members(memberList)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int id = teamBuildingList.getTeamBuildingList().get(0).getTeamBuildingId();
        result = TeamBuildingAcceptanceTestHelper.addNewTeam(mvc, id, objectMapper.writeValueAsString(teamRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-408"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Team name cannot be blank"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 추가 실패 테스트 - user_id가 이미 다른 팀에 존재할 때")
    public void ADD_NEW_TEAM_IN_TEAM_BUILDING_CAUSE_DUPLICATED_USER_IN_OTHER_TEAM_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        List<String> memberList = new ArrayList<>();
        memberList.add("alcuk_id");
        TeamRequest teamRequest = TeamRequest.builder()
                .teamName("frghnjmkl")
                .members(memberList)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int id = teamBuildingList.getTeamBuildingList().get(0).getTeamBuildingId();
        result = TeamBuildingAcceptanceTestHelper.addNewTeam(mvc, id, objectMapper.writeValueAsString(teamRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-409"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Cannot add user \"" + "alcuk_id" + "\" in team cause already in other team"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 추가 실패 테스트 - 권한 없음")
    public void ADD_NEW_TEAM_IN_TEAM_BUILDING_CAUSE_NO_PERMISSION_TEST() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String wrongToken = createUserAndGetToken("lin","linirini","2fhfhfhfh!");
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        List<String> memberList = new ArrayList<>();
        TeamRequest teamRequest = TeamRequest.builder()
                .teamName("chisami")
                .members(memberList)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int id = teamBuildingList.getTeamBuildingList().get(0).getTeamBuildingId();
        result = TeamBuildingAcceptanceTestHelper.addNewTeam(mvc, id, objectMapper.writeValueAsString(teamRequest), wrongToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 수정 성공 테스트")
    public void MODIFY_TEAM_IN_TEAM_BUILDING_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        TeamRequest teamModifyRequest = TeamRequest.builder()
                .teamName("lalalala")
                .members(List.of())
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamId = teamBuildingList.getTeamBuildingList().get(0).getTeamList().get(0).getTeamId();
        result = TeamBuildingAcceptanceTestHelper.modifyTeamInTeamBuilding(mvc, teamId, objectMapper.writeValueAsString(teamModifyRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 수정 실패 테스트 - 중복된 team_name")
    public void MODIFY_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_DUPLICATED_TEAM_NAME_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamId = teamBuildingList.getTeamBuildingList().get(0).getTeamList().get(0).getTeamId();
        String duplicatedTeamName = teamBuildingList.getTeamBuildingList().get(0).getTeamList().get(0).getTeamName();
        TeamRequest teamModifyRequest = TeamRequest.builder()
                .teamName(duplicatedTeamName)
                .members(List.of())
                .build();
        result = TeamBuildingAcceptanceTestHelper.modifyTeamInTeamBuilding(mvc, teamId, objectMapper.writeValueAsString(teamModifyRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-406"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Duplicated team name \"" + duplicatedTeamName + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀 빌딩 그룹 내 팀 수정 실패 테스트 - team_name이 공백")
    public void MODIFY_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_TEAM_NAME_IS_BLANK_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamId = teamBuildingList.getTeamBuildingList().get(0).getTeamList().get(0).getTeamId();
        String teamName = "";
        TeamRequest teamModifyRequest = TeamRequest.builder()
                .teamName(teamName)
                .members(List.of())
                .build();
        result = TeamBuildingAcceptanceTestHelper.modifyTeamInTeamBuilding(mvc, teamId, objectMapper.writeValueAsString(teamModifyRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-408"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Team name cannot be blank"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 수정 실패 테스트 - 이미 그룹 내 다른 팀에 존재하는 user를 팀원으로 추가")
    public void MODIFY_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_ALREADY_EXIST_USER_ID_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        List<String> memberList = new ArrayList<>();
        for (UserWeightRequest user : userList){
            memberList.add(user.getUserId());
        }
        TeamRequest teamModifyRequest = TeamRequest.builder()
                .teamName("lalalala")
                .members(memberList)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamId = teamBuildingList.getTeamBuildingList().get(0).getTeamList().get(0).getTeamId();
        result = TeamBuildingAcceptanceTestHelper.modifyTeamInTeamBuilding(mvc, teamId, objectMapper.writeValueAsString(teamModifyRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-409"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Cannot add user \"alcuk_id\" in team cause already in other team"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 수정 실패 테스트 - 없는 team_id")
    public void MODIFY_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_UNKNOWN_TEAM_ID_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        TeamRequest teamModifyRequest = TeamRequest.builder()
                .teamName("lalalala")
                .members(List.of())
                .build();

        //when
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamId = 0;
        result = TeamBuildingAcceptanceTestHelper.modifyTeamInTeamBuilding(mvc, teamId, objectMapper.writeValueAsString(teamModifyRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-407"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Cannot find team id \"" + teamId + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 수정 실패 테스트 - 권한 없음")
    public void MODIFY_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String wrongToken = createUserAndGetToken("lin","linirini","2fhfhfhfh!");
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        TeamRequest teamModifyRequest = TeamRequest.builder()
                .teamName("lalalala")
                .members(List.of())
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamId = teamBuildingList.getTeamBuildingList().get(0).getTeamList().get(0).getTeamId();
        result = TeamBuildingAcceptanceTestHelper.modifyTeamInTeamBuilding(mvc, teamId, objectMapper.writeValueAsString(teamModifyRequest), wrongToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 수정 성공 테스트")
    public void MODIFY_TEAM_BUILDING_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        String modifyTitle = "lalaalalalalaalaa";
        TeamBuildingUpdateRequest teamBuildingUpdateRequest = TeamBuildingUpdateRequest.builder()
                .teamBuildingTitle(modifyTitle)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamBuildingId = teamBuildingList.getTeamBuildingList().get(0).getTeamBuildingId();
        result = TeamBuildingAcceptanceTestHelper.modifyTeamBuilding(mvc, teamBuildingId, objectMapper.writeValueAsString(teamBuildingUpdateRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 수정 실패 테스트 - teambuilding_title 1000자 초과")
    public void MODIFY_TEAM_BUILDING_FAIL_CAUSE_TEAM_BUILDING_TITLE_OVERFLOW_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        String modifyTitle = createOverflow();
        TeamBuildingUpdateRequest teamBuildingUpdateRequest = TeamBuildingUpdateRequest.builder()
                .teamBuildingTitle(modifyTitle)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamBuildingId = teamBuildingList.getTeamBuildingList().get(0).getTeamBuildingId();
        result = TeamBuildingAcceptanceTestHelper.modifyTeamBuilding(mvc, teamBuildingId, objectMapper.writeValueAsString(teamBuildingUpdateRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-401"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Teambuilding title cannot be more than 1000 current length \"" + modifyTitle.length() + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 수정 실패 테스트 - teambuilding_title이 공백")
    public void MODIFY_TEAM_BUILDING_FAIL_CAUSE_TEAM_BUILDING_TITLE_IS_BLANK_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        String modifyTitle = "";
        TeamBuildingUpdateRequest teamBuildingUpdateRequest = TeamBuildingUpdateRequest.builder()
                .teamBuildingTitle(modifyTitle)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamBuildingId = teamBuildingList.getTeamBuildingList().get(0).getTeamBuildingId();
        result = TeamBuildingAcceptanceTestHelper.modifyTeamBuilding(mvc, teamBuildingId, objectMapper.writeValueAsString(teamBuildingUpdateRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Teambuilding title cannot be blank"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 수정 실패 테스트 - 없는 teambuilding_id")
    public void MODIFY_TEAM_BUILDING_FAIL_CAUSE_UNKNOWN_TEAM_BUILDING_ID_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        String modifyTitle = "lalaalalalalaalaa";
        TeamBuildingUpdateRequest teamBuildingUpdateRequest = TeamBuildingUpdateRequest.builder()
                .teamBuildingTitle(modifyTitle)
                .build();

        //when
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        int id = 0;
        result = TeamBuildingAcceptanceTestHelper.modifyTeamBuilding(mvc, id, objectMapper.writeValueAsString(teamBuildingUpdateRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-400"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Cannot find teambuilding id \"" + id + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 수정 실패 테스트 - 권한 없음")
    public void MODIFY_TEAM_BUILDING_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String wrongToken = createUserAndGetToken("lin","linirini","2fhfhfhfh!");
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        String modifyTitle = "lalaalalalalaalaa";
        TeamBuildingUpdateRequest teamBuildingUpdateRequest = TeamBuildingUpdateRequest.builder()
                .teamBuildingTitle(modifyTitle)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamBuildingId = teamBuildingList.getTeamBuildingList().get(0).getTeamBuildingId();
        result = TeamBuildingAcceptanceTestHelper.modifyTeamBuilding(mvc, teamBuildingId, objectMapper.writeValueAsString(teamBuildingUpdateRequest), wrongToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀이름 수정 성공 테스트")
    public void MODIFY_TEAM_NAME_IN_TEAM_BUILDING_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        TeamUpdateRequest teamUpdateRequest = TeamUpdateRequest.builder()
                .teamName("chisam!")
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamId = teamBuildingList.getTeamBuildingList().get(0).getTeamList().get(0).getTeamId();
        result = TeamBuildingAcceptanceTestHelper.modifyTeamNameInTeamBuilding(mvc, teamId, objectMapper.writeValueAsString(teamUpdateRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀이름 수정 실패 테스트 - 없는 team_id")
    public void MODIFY_TEAM_NAME_IN_TEAM_BUILDING_FAIL_CAUSE_UNKNOWN_TEAM_ID_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        TeamUpdateRequest teamUpdateRequest = TeamUpdateRequest.builder()
                .teamName("chisam!")
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int wrongId = 0;
        result = TeamBuildingAcceptanceTestHelper.modifyTeamNameInTeamBuilding(mvc, wrongId, objectMapper.writeValueAsString(teamUpdateRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-407"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Cannot find team id \"" + wrongId + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀이름 수정 실패 테스트 - team_name 중복")
    public void MODIFY_TEAM_NAME_IN_TEAM_BUILDING_FAIL_CAUSE_DUPLICATED_TEAM_NAME_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        TeamUpdateRequest teamUpdateRequest = TeamUpdateRequest.builder()
                .teamName("chisam!")
                .build();
        TeamUpdateRequest teamUpdateRequest2 = TeamUpdateRequest.builder()
                .teamName("chisam!")
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamId = teamBuildingList.getTeamBuildingList().get(0).getTeamList().get(0).getTeamId();
        TeamBuildingAcceptanceTestHelper.modifyTeamNameInTeamBuilding(mvc, teamId, objectMapper.writeValueAsString(teamUpdateRequest), adminToken);
        result = TeamBuildingAcceptanceTestHelper.modifyTeamNameInTeamBuilding(mvc, teamId, objectMapper.writeValueAsString(teamUpdateRequest2), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-406"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Duplicated team name \"" + teamUpdateRequest2.getTeamName() + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀이름 수정 실패 테스트 - team_name이 공백")
    public void MODIFY_TEAM_NAME_IN_TEAM_BUILDING_FAIL_CAUSE_TEAM_NAME_IS_BLANK_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        TeamUpdateRequest teamUpdateRequest = TeamUpdateRequest.builder()
                .teamName("")
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 2, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamId = teamBuildingList.getTeamBuildingList().get(0).getTeamList().get(0).getTeamId();
        result = TeamBuildingAcceptanceTestHelper.modifyTeamNameInTeamBuilding(mvc, teamId, objectMapper.writeValueAsString(teamUpdateRequest), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-408"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Team name cannot be blank"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀이름 수정 실패 테스트 - 권한 없음")
    public void MODIFY_TEAM_NAME_IN_TEAM_BUILDING_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String wrongToken = createUserAndGetToken("lin","linirini","2fhfhfhfh!");
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();
        TeamUpdateRequest teamUpdateRequest = TeamUpdateRequest.builder()
                .teamName("chisam!")
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 1, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamId = teamBuildingList.getTeamBuildingList().get(0).getTeamList().get(0).getTeamId();
        result = TeamBuildingAcceptanceTestHelper.modifyTeamNameInTeamBuilding(mvc, teamId, objectMapper.writeValueAsString(teamUpdateRequest), wrongToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 삭제 성공 테스트")
    public void DELETE_SPECIFIC_TEAM_BUILDING_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 1, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamBuildingId = teamBuildingList.getTeamBuildingList().get(0).getTeamBuildingId();
        result = TeamBuildingAcceptanceTestHelper.deleteTeamBuilding(mvc, teamBuildingId, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("팀빌딩 그룹 삭제 실패 테스트 - 없는 teambuilding_id")
    public void DELETE_SPECIFIC_TEAM_BUILDING_FAIL_CAUSE_UNKNOWN_TEAM_BUILDING_ID_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        List<UserWeightRequest> userList2 = createUserWeightRequestList2();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        int wrongId = 0;
        ResultActions result = TeamBuildingAcceptanceTestHelper.deleteTeamBuilding(mvc, wrongId, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-400"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Cannot find teambuilding id \"" + wrongId + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("팀빌딩 그룹 삭제 실패 테스트 - 권한 없음")
    public void DELETE_SPECIFIC_TEAM_BUILDING_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String wrongToken = createUserAndGetToken("lin","linirini","2fhfhfhfh!");
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 1, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamBuildingId = teamBuildingList.getTeamBuildingList().get(0).getTeamBuildingId();
        result = TeamBuildingAcceptanceTestHelper.deleteTeamBuilding(mvc, teamBuildingId, wrongToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 삭제 성공 테스트")
    public void DELETE_SPECIFIC_TEAM_IN_TEAM_BUILDING_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 1, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamId = teamBuildingList.getTeamBuildingList().get(0).getTeamList().get(0).getTeamId();
        result = TeamBuildingAcceptanceTestHelper.deleteTeamInTeamBuilding(mvc, teamId, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 삭제 실패 테스트 - 없는 team_id")
    public void DELETE_SPECIFIC_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_UNKNOWN_TEAM_ID_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 1, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int wrongId = 0;
        result = TeamBuildingAcceptanceTestHelper.deleteTeamInTeamBuilding(mvc, wrongId, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-407"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Cannot find team id \"" + wrongId + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 삭제 실패 테스트 - 권한 없음")
    public void DELETE_SPECIFIC_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String wrongToken = createUserAndGetToken("lin","lini","linirini!!2");
        String title = "2nd Algorithm Contest Team";
        int teamCount = 2;
        List<UserWeightRequest> userList = createUserWeightRequestList();
        TeamBuildingRequest teamBuildingRequest = TeamBuildingRequest.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userList(userList)
                .build();

        //when
        TeamBuildingAcceptanceTestHelper.createTeamBuilding(mvc, objectMapper.writeValueAsString(teamBuildingRequest), adminToken);
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 1, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamId = teamBuildingList.getTeamBuildingList().get(0).getTeamList().get(0).getTeamId();
        result = TeamBuildingAcceptanceTestHelper.deleteTeamInTeamBuilding(mvc, teamId, wrongToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    private List<UserWeightRequest> createUserWeightRequestList() throws Exception{
        List<UserWeightRequest> userList = new ArrayList<>();
        userList.add(createUserByUserId("alcuk_id", 3));
        userList.add(createUserByUserId("alcuk_id2", 2));
        userList.add(createUserByUserId("alcuk_id3", 1));
        userList.add(createUserByUserId("alcuk_id4", 4));
        userList.add(createUserByUserId("alcuk_id5", 2));
        return userList;
    }

    private List<UserWeightRequest> createUserWeightRequestList2() throws Exception{
        List<UserWeightRequest> userList = new ArrayList<>();
        userList.add(createUserByUserId("alcuk_id6", 5));
        userList.add(createUserByUserId("alcuk_id7", 2));
        userList.add(createUserByUserId("alcuk_id8", 9));
        userList.add(createUserByUserId("alcuk_id9", 3));
        userList.add(createUserByUserId("alcuk_id10", 8));
        return userList;
    }

    private UserWeightRequest createUserByUserId(String userId, int weight) throws Exception{
        createUser(userId);
        return UserWeightRequest.builder()
                .userId(userId)
                .weight(weight)
                .build();
    }

    private void createUser(String userId) throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        UserRequest userRequest = UserRequest.builder()
                .name(userId)
                .userId(userId)
                .userPassword("2dfgsfvgdd!")
                .phoneNumber("01012341234")
                .build();
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest));
        UserAcceptanceTestHelper.approveJoinRequest(mvc,adminToken,userRequest.getUserId());
        userRequestList.add(userRequest);
    }

    private String createOverflow(){
        String title = "";
        for (int i = 0; i < 1005; i++){
            title = title + "A";
        }
        return title;
    }

    private String createUserAndGetToken(String name, String userId, String userPassword) throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        UserRequest userRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber("01012341234")
                .build();
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest)).andDo(MockMvcResultHandlers.print());
        UserAcceptanceTestHelper.approveJoinRequest(mvc,adminToken,userRequest.getUserId());
        userRequestList.add(userRequest);

        return AuthenticationAcceptanceTestHelper.getToken(mvc, objectMapper, AuthTokenRequest.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build());
    }

}
