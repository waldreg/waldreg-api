package org.waldreg.acceptance.teambuilding;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
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
import org.waldreg.controller.joiningpool.request.UserRequest;
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
                MockMvcResultMatchers.jsonPath("$.messages").value("User's Weight value should be between 1 and 10, current weight \"" + userList.get(0).getWeight() + "\""),
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
        String wrongToken = "";
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
                MockMvcResultMatchers.status().isBadRequest(),
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
                MockMvcResultMatchers.jsonPath("$.teambuildings.[0].teambuilding_title").value(teamBuildingRequest.getTeamBuildingTitle()),
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
                MockMvcResultMatchers.jsonPath("$.teambuildings.[2].teambuilding_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[2].teambuilding_title").value(teamBuildingRequest2.getTeamBuildingTitle()),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[2].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[2].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[2].teams.[0].team_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[2].teams.[0].team_name").isString(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[2].teams.[0].last_modified_at").isString(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[2].teams.[0].team_members.[0].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[2].teams.[0].team_members.[0].user_id").isString(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[2].teams.[0].team_members.[0].name").isString(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[2].teams.[1].team_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[2].teams.[1].team_members.[0]").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[2].teams.[1].team_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.teambuildings.[2].teams.[2].team_members.[0]").isNotEmpty()
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
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid range start-idx \"" + startIdx + "\" end-idx \"" + endIdx + "\""),
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
                MockMvcResultMatchers.jsonPath("$.teambuilding_title").value(teamBuildingRequest.getTeamBuildingTitle()),
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
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-409"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Cannot add user \"" + "alcuk_id" + "\" in team cause already in other team"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 추가 실패 테스트 - 권한 없음")
    public void ADD_NEW_TEAM_IN_TEAM_BUILDING_CAUSE_NO_PERMISSION_TEST() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String wrongToken = "";
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
                MockMvcResultMatchers.status().isBadRequest(),
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
                MockMvcResultMatchers.jsonPath("$.messages").value("\"" + "alcuk_id" + "\" already belongs to another team"),
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
        String wrongToken = "";
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
        int teamId = teamBuildingList.getTeamBuildingList().get(0).getTeamList().get(0).getTeamId();
        result = TeamBuildingAcceptanceTestHelper.modifyTeamInTeamBuilding(mvc, teamId, objectMapper.writeValueAsString(teamModifyRequest), wrongToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
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
        String wrongToken = "";
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
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamBuildingId = teamBuildingList.getTeamBuildingList().get(0).getTeamBuildingId();
        result = TeamBuildingAcceptanceTestHelper.modifyTeamBuilding(mvc, teamBuildingId, objectMapper.writeValueAsString(teamBuildingUpdateRequest), wrongToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
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
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-400"),
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
                MockMvcResultMatchers.jsonPath("$.code").value("TEAMBUILDING-406"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Team name cannot be blank"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀이름 수정 실패 테스트 - 권한 없음")
    public void MODIFY_TEAM_NAME_IN_TEAM_BUILDING_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String wrongToken = "";
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
        ResultActions result = TeamBuildingAcceptanceTestHelper.inquiryAllTeamBuilding(mvc, 1, 1, adminToken);
        TeamBuildingListResponse teamBuildingList = objectMapper.readValue(result
                .andReturn()
                .getResponse()
                .getContentAsString(), TeamBuildingListResponse.class);
        int teamId = teamBuildingList.getTeamBuildingList().get(0).getTeamList().get(0).getTeamId();
        result = TeamBuildingAcceptanceTestHelper.modifyTeamNameInTeamBuilding(mvc, teamId, objectMapper.writeValueAsString(teamUpdateRequest), wrongToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
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
        String wrongToken = "";
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
                MockMvcResultMatchers.status().isBadRequest(),
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
        String wrongToken = "";
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
                MockMvcResultMatchers.status().isBadRequest(),
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
        UserRequest userRequest = UserRequest.builder()
                .name("alcuk")
                .userId(userId)
                .userPassword("2dfgsfvgdd!")
                .phoneNumber("010-1234-1234")
                .build();
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest));
        userRequestList.add(userRequest);
    }

    private String createOverflow(){
        String title = "";
        for (int i = 0; i < 1005; i++){
            title = title + "A";
        }
        return title;
    }

    public static class TeamBuildingRequest{

        @JsonProperty("teambuilding_title")
        private String teamBuildingTitle;
        @JsonProperty("team_count")
        private int teamCount;
        @JsonProperty("users")
        private List<UserWeightRequest> userList;

        private TeamBuildingRequest(){}

        private TeamBuildingRequest(Builder builder){
            this.teamBuildingTitle = builder.teamBuildingTitle;
            this.teamCount = builder.teamCount;
            this.userList = builder.userList;
        }

        public static Builder builder(){return new Builder();}

        public String getTeamBuildingTitle(){
            return teamBuildingTitle;
        }

        public int getTeamCount(){
            return teamCount;
        }

        public List<UserWeightRequest> getUserList(){
            return userList;
        }

        public final static class Builder{

            private String teamBuildingTitle;
            private int teamCount;
            private List<UserWeightRequest> userList;

            private Builder(){}

            public Builder teamBuildingTitle(String teamBuildingTitle){
                this.teamBuildingTitle = teamBuildingTitle;
                return this;
            }

            public Builder teamCount(int teamCount){
                this.teamCount = teamCount;
                return this;
            }

            public Builder userList(List<UserWeightRequest> userList){
                this.userList = userList;
                return this;
            }

            public TeamBuildingRequest build(){return new TeamBuildingRequest(this);}

        }

    }

    public static class TeamRequest{

        @JsonProperty("team_name")
        private String teamName;
        @JsonProperty("members")
        private List<String> members;

        private TeamRequest(){}

        private TeamRequest(Builder builder){
            this.teamName = builder.teamName;
            this.members = builder.members;
        }

        public static Builder builder(){return new Builder();}

        public String getTeamName(){
            return teamName;
        }

        public List<String> getMembers(){
            return members;
        }

        public final static class Builder{

            private String teamName;
            private List<String> members;

            public Builder teamName(String teamName){
                this.teamName = teamName;
                return this;
            }

            public Builder members(List<String> members){
                this.members = members;
                return this;
            }

            public TeamRequest build(){return new TeamRequest(this);}

        }

    }

    public static class TeamBuildingUpdateRequest{

        @JsonProperty("teambuilding_title")
        private String teamBuildingTitle;

        private TeamBuildingUpdateRequest(){}

        private TeamBuildingUpdateRequest(Builder builder){
            this.teamBuildingTitle = builder.teamBuildingTitle;
        }

        public static Builder builder(){return new Builder();}

        public String getTeamBuildingTitle(){
            return teamBuildingTitle;
        }

        public final static class Builder{

            private String teamBuildingTitle;

            public Builder teamBuildingTitle(String teamBuildingTitle){
                this.teamBuildingTitle = teamBuildingTitle;
                return this;
            }

            public TeamBuildingUpdateRequest build(){return new TeamBuildingUpdateRequest(this);}

        }

    }

    public static class TeamUpdateRequest{

        @JsonProperty("team_name")
        private String teamName;

        private TeamUpdateRequest(){}

        private TeamUpdateRequest(Builder builder){
            this.teamName = builder.teamName;
        }

        public static Builder builder(){return new Builder();}

        public String getTeamName(){
            return teamName;
        }

        public final static class Builder{

            private String teamName;

            public Builder teamName(String teamName){
                this.teamName = teamName;
                return this;
            }

            public TeamUpdateRequest build(){return new TeamUpdateRequest(this);}

        }

    }

    public static class UserWeightRequest{

        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("weight")
        private int weight;

        private UserWeightRequest(){}

        private UserWeightRequest(Builder builder){
            this.userId = builder.userId;
            this.weight = builder.weight;
        }

        public static Builder builder(){return new Builder();}

        public String getUserId(){
            return userId;
        }

        public int getWeight(){
            return weight;
        }

        public void setUserId(String userId){
            this.userId = userId;
        }

        public void setWeight(int weight){
            this.weight = weight;
        }

        public final static class Builder{

            private String userId;
            private int weight;

            private Builder(){}

            public Builder userId(String userId){
                this.userId = userId;
                return this;
            }

            public Builder weight(int weight){
                this.weight = weight;
                return this;
            }

            public UserWeightRequest build(){return new UserWeightRequest(this);}

        }

    }

    public static class TeamBuildingListResponse{

        @JsonProperty("max_idx")
        private int maxIdx;
        @JsonProperty("teambuildings")
        private List<TeamBuildingResponse> teamBuildingList;

        private TeamBuildingListResponse(){}

        private TeamBuildingListResponse(Builder builder){
            this.maxIdx = builder.maxIdx;
            this.teamBuildingList = builder.teamBuildingList;
        }

        public static Builder builder(){return new Builder();}

        public int getMaxIdx(){
            return maxIdx;
        }

        public List<TeamBuildingResponse> getTeamBuildingList(){
            return teamBuildingList;
        }

        public final static class Builder{

            private int maxIdx;
            private List<TeamBuildingResponse> teamBuildingList;

            private Builder(){}

            public Builder maxIdx(int maxIdx){
                this.maxIdx = maxIdx;
                return this;
            }

            public Builder teamBuildingList(List<TeamBuildingResponse> teamBuildingList){
                this.teamBuildingList = teamBuildingList;
                return this;
            }

            public TeamBuildingListResponse build(){return new TeamBuildingListResponse(this);}

        }

    }

    public static class TeamBuildingResponse{

        @JsonProperty("teambuilding_id")
        private int teamBuildingId;
        @JsonProperty("teambuilding_title")
        private String teamBuildingTitle;
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
        @JsonProperty("last_modified_at")
        private LocalDateTime lastModifiedAt;
        @JsonProperty("teams")
        private List<TeamResponse> teamList;

        private TeamBuildingResponse(){}

        private TeamBuildingResponse(Builder builder){
            this.teamBuildingId = builder.teamBuildingId;
            this.teamBuildingTitle = builder.teamBuildingTitle;
            this.createdAt = builder.createdAt;
            this.lastModifiedAt = builder.lastModifiedAt;
            this.teamList = builder.teamList;
        }

        public static Builder builder(){return new Builder();}

        public int getTeamBuildingId(){
            return teamBuildingId;
        }

        public String getTeamBuildingTitle(){
            return teamBuildingTitle;
        }

        public LocalDateTime getCreatedAt(){
            return createdAt;
        }

        public LocalDateTime getLastModifiedAt(){
            return lastModifiedAt;
        }

        public List<TeamResponse> getTeamList(){
            return teamList;
        }

        public final static class Builder{

            private int teamBuildingId;
            private String teamBuildingTitle;
            private LocalDateTime createdAt;
            private LocalDateTime lastModifiedAt;
            private List<TeamResponse> teamList;

            public Builder teamBuildingId(int teamBuildingId){
                this.teamBuildingId = teamBuildingId;
                return this;
            }

            public Builder teamBuildingTitle(String teamBuildingTitle){
                this.teamBuildingTitle = teamBuildingTitle;
                return this;
            }

            public Builder createdAt(LocalDateTime createdAt){
                this.createdAt = createdAt;
                return this;
            }

            public Builder lastModifiedAt(LocalDateTime lastModifiedAt){
                this.lastModifiedAt = lastModifiedAt;
                return this;
            }

            public Builder teamList(List<TeamResponse> teamList){
                this.teamList = teamList;
                return this;
            }

            public TeamBuildingResponse build(){return new TeamBuildingResponse(this);}

        }

    }

    public static class TeamResponse{

        @JsonProperty("team_id")
        private int teamId;
        @JsonProperty("team_name")
        private String teamName;
        @JsonProperty("last_modified_at")
        private LocalDateTime lastModifiedAt;
        @JsonProperty("team_members")
        private List<MemberResponse> memberList;

        private TeamResponse(){}

        private TeamResponse(Builder builder){
            this.teamId = builder.teamId;
            this.teamName = builder.teamName;
            this.lastModifiedAt = builder.lastModifiedAt;
            this.memberList = builder.memberList;
        }

        public static Builder builder(){return new Builder();}

        public int getTeamId(){
            return teamId;
        }

        public String getTeamName(){
            return teamName;
        }

        public LocalDateTime getLastModifiedAt(){
            return lastModifiedAt;
        }

        public List<MemberResponse> getMemberList(){
            return memberList;
        }

        public final static class Builder{

            private int teamId;
            private String teamName;
            private LocalDateTime lastModifiedAt;
            private List<MemberResponse> memberList;

            public Builder teamId(int teamId){
                this.teamId = teamId;
                return this;
            }

            public Builder teamName(String teamName){
                this.teamName = teamName;
                return this;
            }

            public Builder lastModified(LocalDateTime lastModifiedAt){
                this.lastModifiedAt = lastModifiedAt;
                return this;
            }

            public Builder memberList(List<MemberResponse> memberList){
                this.memberList = memberList;
                return this;
            }

            public TeamResponse build(){return new TeamResponse(this);}

        }

    }

    public static class MemberResponse{

        @JsonProperty("id")
        private int id;
        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("name")
        private String name;

        private MemberResponse(){}

        private MemberResponse(Builder builder){
            this.id = builder.id;
            this.userId = builder.userId;
            this.name = builder.name;
        }

        public static Builder builder(){return new Builder();}

        public int getId(){
            return id;
        }

        public String getUserId(){
            return userId;
        }

        public String getName(){
            return name;
        }

        public final static class Builder{

            private int id;
            private String userId;
            private String name;

            public Builder id(int id){
                this.id = id;
                return this;
            }

            public Builder userId(String userId){
                this.userId = userId;
                return this;
            }

            public Builder name(String name){
                this.name = name;
                return this;
            }

            public MemberResponse build(){return new MemberResponse(this);}

        }

    }

}
