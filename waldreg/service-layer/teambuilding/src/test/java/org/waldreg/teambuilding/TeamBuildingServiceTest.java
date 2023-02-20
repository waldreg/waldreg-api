package org.waldreg.teambuilding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.dto.TeamBuildingRequestDto;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.dto.UserRequestDto;
import org.waldreg.exception.ContentOverflowException;
import org.waldreg.exception.InvalidRangeException;
import org.waldreg.exception.InvalidTeamCountException;
import org.waldreg.exception.InvalidUserWeightException;
import org.waldreg.exception.UnknownTeamBuildingIdException;
import org.waldreg.exception.UnknownUserIdException;
import org.waldreg.teambuilding.management.DefaultTeamBuildingManager;
import org.waldreg.teambuilding.management.TeamBuildingManager;
import org.waldreg.teambuilding.management.teamcreator.TeamCreator;
import org.waldreg.teambuilding.spi.TeamBuildingRepository;
import org.waldreg.teambuilding.spi.TeamBuildingUserRepository;
import org.waldreg.teambuilding.spi.TeamBuildingsTeamRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultTeamBuildingManager.class, TeamCreator.class})
public class TeamBuildingServiceTest{

    @Autowired
    TeamBuildingManager teamBuildingManager;

    @MockBean
    TeamBuildingRepository teamBuildingRepository;

    @MockBean
    TeamBuildingUserRepository teamBuildingUserRepository;

    @MockBean
    TeamBuildingsTeamRepository teamBuildingsTeamRepository;

    @Test
    @DisplayName("새로운 팀빌딩 그룹 생성 성공 테스트")
    public void CREATE_NEW_TEAM_BUILDING_SUCCESS_TEST(){
        //given
        String title = "2nd Algorithm Contest Team";
        int teamCount = 3;
        List<UserRequestDto> userRequestDtoList = createUserRequestDtoList();
        TeamBuildingRequestDto teamBuildingRequestDto = TeamBuildingRequestDto.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userRequestDtoList(userRequestDtoList)
                .build();
        TeamBuildingDto teamBuildingDto = TeamBuildingDto.builder()
                .teamBuildingId(1)
                .teamBuildingTitle(title)
                .build();

        //when
        Mockito.when(teamBuildingUserRepository.isExistUserByUserId(Mockito.anyString())).thenReturn(true);
        Mockito.when(teamBuildingRepository.createTeamBuilding(Mockito.any())).thenReturn(teamBuildingDto);

        //then
        Assertions.assertDoesNotThrow(() -> teamBuildingManager.createTeamBuilding(teamBuildingRequestDto));

    }

    @Test
    @DisplayName("새로운 팀빌딩 그룹 생성 실패 테스트 - teamBuildingTitle 길이 1000 초과")
    public void CREATE_NEW_TEAM_BUILDING_FAIL_CAUSE_TEAM_BUILDING_TITLE_OVERFLOW_TEST(){
        //given
        String title = createOverflow();
        int teamCount = 3;
        List<UserRequestDto> userRequestDtoList = createUserRequestDtoList();
        TeamBuildingRequestDto teamBuildingRequestDto = TeamBuildingRequestDto.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userRequestDtoList(userRequestDtoList)
                .build();

        //when
        Mockito.when(teamBuildingUserRepository.isExistUserByUserId(Mockito.anyString())).thenReturn(true);

        //then
        Assertions.assertThrows(ContentOverflowException.class, () -> teamBuildingManager.createTeamBuilding(teamBuildingRequestDto));

    }

    @Test
    @DisplayName("새로운 팀빌딩 그룹 생성 실패 테스트 - 팀 개수가 0보다 작거나 같음")
    public void CREATE_NEW_TEAM_BUILDING_FAIL_CAUSE_TEAM_COUNT_LESS_THAN_OR_EQUAL_TO_ZERO_TEST(){
        //given
        String title = "2nd Algorithm Contest Team";
        int teamCount = 0;
        List<UserRequestDto> userRequestDtoList = createUserRequestDtoList();
        TeamBuildingRequestDto teamBuildingRequestDto = TeamBuildingRequestDto.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userRequestDtoList(userRequestDtoList)
                .build();

        //when
        Mockito.when(teamBuildingUserRepository.isExistUserByUserId(Mockito.anyString())).thenReturn(true);

        //then
        Assertions.assertThrows(InvalidTeamCountException.class, () -> teamBuildingManager.createTeamBuilding(teamBuildingRequestDto));

    }

    @Test
    @DisplayName("새로운 팀빌딩 그룹 생성 실패 테스트 - 잘못된 user weight")
    public void CREATE_NEW_TEAM_BUILDING_FAIL_CAUSE_INVALID_USER_WEIGHT_TEST(){
        //given
        String title = "2nd Algorithm Contest Team";
        int teamCount = 3;
        List<UserRequestDto> userRequestDtoList = createWrongUserRequestDtoList();
        TeamBuildingRequestDto teamBuildingRequestDto = TeamBuildingRequestDto.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userRequestDtoList(userRequestDtoList)
                .build();

        //when
        Mockito.when(teamBuildingUserRepository.isExistUserByUserId(Mockito.anyString())).thenReturn(true);

        //then
        Assertions.assertThrows(InvalidUserWeightException.class, () -> teamBuildingManager.createTeamBuilding(teamBuildingRequestDto));

    }

    @Test
    @DisplayName("새로운 팀빌딩 그룹 생성 실패 테스트 - 잘못된 team_count(userCount : 6)")
    public void CREATE_NEW_TEAM_BUILDING_FAIL_CAUSE_INVALID_TEAM_COUNT_TEST(){
        //given
        String title = "2nd Algorithm Contest Team";
        int teamCount = 10;
        List<UserRequestDto> userRequestDtoList = createUserRequestDtoList();
        TeamBuildingRequestDto teamBuildingRequestDto = TeamBuildingRequestDto.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userRequestDtoList(userRequestDtoList)
                .build();

        //when
        Mockito.when(teamBuildingUserRepository.isExistUserByUserId(Mockito.anyString())).thenReturn(true);

        //then
        Assertions.assertThrows(InvalidTeamCountException.class, () -> teamBuildingManager.createTeamBuilding(teamBuildingRequestDto));

    }

    @Test
    @DisplayName("새로운 팀빌딩 그룹 생성 실패 테스트 - 없는 userId")
    public void CREATE_NEW_TEAM_BUILDING_FAIL_CAUSE_UNKNOWN_USER_ID_TEST(){
        //given
        String title = "2nd Algorithm Contest Team";
        int teamCount = 3;
        List<UserRequestDto> userRequestDtoList = createUserRequestDtoList();
        TeamBuildingRequestDto teamBuildingRequestDto = TeamBuildingRequestDto.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userRequestDtoList(userRequestDtoList)
                .build();

        //when
        Mockito.when(teamBuildingUserRepository.isExistUserByUserId(Mockito.anyString())).thenReturn(false);

        //then
        Assertions.assertThrows(UnknownUserIdException.class, () -> teamBuildingManager.createTeamBuilding(teamBuildingRequestDto));

    }

    @Test
    @DisplayName("특정 팀빌딩 그룹 조회 성공 테스트")
    public void INQUIRY_SPECIFIC_TEAM_BUILDING_SUCCESS_TEST(){
        //given
        int teamBuildingId = 1;
        TeamBuildingDto teamBuildingDto = createTeamBuildingDto(teamBuildingId);

        //when
        Mockito.when(teamBuildingRepository.isExistTeamBuilding(Mockito.anyInt())).thenReturn(true);
        Mockito.when(teamBuildingRepository.readTeamBuildingById(Mockito.anyInt())).thenReturn(teamBuildingDto);
        TeamBuildingDto result = teamBuildingManager.readTeamBuildingById(1);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(teamBuildingDto.getTeamBuildingId(), result.getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamBuildingTitle(), result.getTeamBuildingTitle()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().size(), result.getTeamList().size()),
                () -> Assertions.assertEquals(teamBuildingDto.getLastModifiedAt(), result.getLastModifiedAt())
        );

    }

    @Test
    @DisplayName("특정 팀빌딩 그룹 조회 실패 테스트 - 없는 TeambuidlingId")
    public void INQUIRY_SPECIFIC_TEAM_BUILDING_FAIL_CAUSE_UNKNOWN_TEAM_BUILDING_ID_TEST(){
        //given
        int teamBuildingId = 0;
        TeamBuildingDto teamBuildingDto = createTeamBuildingDto(teamBuildingId);

        //when
        Mockito.when(teamBuildingRepository.isExistTeamBuilding(Mockito.anyInt())).thenReturn(false);
        Mockito.when(teamBuildingRepository.readTeamBuildingById(Mockito.anyInt())).thenReturn(teamBuildingDto);

        //then
        Assertions.assertThrows(UnknownTeamBuildingIdException.class, () -> teamBuildingManager.readTeamBuildingById(1));

    }

    @Test
    @DisplayName("팀빌딩 그룹 전체 조회 성공 테스트")
    public void INQUIRY_ALL_TEAM_BUILDING_SUCCESS_TEST(){
        //given
        int teamBuildingId = 1;
        int teamBuildingId2 = 2;
        TeamBuildingDto teamBuildingDto = createTeamBuildingDto(teamBuildingId);
        TeamBuildingDto teamBuildingDto2 = createTeamBuildingDto(teamBuildingId2);
        List<TeamBuildingDto> teamBuildingDtoList = new ArrayList<>();
        teamBuildingDtoList.add(teamBuildingDto);
        teamBuildingDtoList.add(teamBuildingDto2);

        //when
        Mockito.when(teamBuildingRepository.readAllTeamBuilding(Mockito.anyInt(), Mockito.anyInt())).thenReturn(teamBuildingDtoList);
        List<TeamBuildingDto> result = teamBuildingManager.readAllTeamBuilding(1, 3);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.size()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamBuildingTitle(), result.get(0).getTeamBuildingTitle()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().size(), result.get(0).getTeamList().size()),
                () -> Assertions.assertEquals(teamBuildingDto.getLastModifiedAt(), result.get(0).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto2.getTeamBuildingTitle(), result.get(1).getTeamBuildingTitle()),
                () -> Assertions.assertEquals(teamBuildingDto2.getTeamList().size(), result.get(1).getTeamList().size()),
                () -> Assertions.assertEquals(teamBuildingDto2.getLastModifiedAt(), result.get(1).getLastModifiedAt())
        );

    }

    @Test
    @DisplayName("팀빌딩 그룹 전체 조회 실패 테스트 - 잘못된 범위")
    public void INQUIRY_ALL_TEAM_BUILDING_FAIL_CAUSE_INVALID_RANGE_TEST(){
        //given
        int teamBuildingId = 1;
        int teamBuildingId2 = 2;
        TeamBuildingDto teamBuildingDto = createTeamBuildingDto(teamBuildingId);
        TeamBuildingDto teamBuildingDto2 = createTeamBuildingDto(teamBuildingId2);
        List<TeamBuildingDto> teamBuildingDtoList = new ArrayList<>();
        teamBuildingDtoList.add(teamBuildingDto);
        teamBuildingDtoList.add(teamBuildingDto2);

        //when
        Mockito.when(teamBuildingRepository.readAllTeamBuilding(Mockito.anyInt(), Mockito.anyInt())).thenReturn(teamBuildingDtoList);

        //then
        Assertions.assertThrows(InvalidRangeException.class, () -> teamBuildingManager.readAllTeamBuilding(2, 1));

    }

    @Test
    @DisplayName("팀빌딩 그룹 수정 성공 테스트")
    public void MODIFY_TEAM_BUILDING_SUCCESS_TEST(){
        //given
        int teamBuildingId = 1;
        TeamBuildingDto teamBuildingDto = createTeamBuildingDto(teamBuildingId);
        TeamBuildingRequestDto teamBuildingRequestDto = TeamBuildingRequestDto.builder()
                .teamBuildingTitle("modified Title")
                .build();
        TeamBuildingDto teamBuildingDto2 = teamBuildingDto;

        //when
        Mockito.when(teamBuildingRepository.isExistTeamBuilding(Mockito.anyInt())).thenReturn(true);
        Mockito.when(teamBuildingRepository.readTeamBuildingById(Mockito.anyInt())).thenReturn(teamBuildingDto2);
        teamBuildingManager.updateTeamBuildingTitleById(1, teamBuildingRequestDto.getTeamBuildingTitle());
        teamBuildingDto2.setTeamBuildingTitle(teamBuildingRequestDto.getTeamBuildingTitle());
        TeamBuildingDto result = teamBuildingManager.readTeamBuildingById(1);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(teamBuildingDto2.getTeamBuildingId(), result.getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto2.getTeamBuildingTitle(), result.getTeamBuildingTitle()),
                () -> Assertions.assertEquals(teamBuildingDto2.getTeamList().size(), result.getTeamList().size()),
                () -> Assertions.assertEquals(teamBuildingDto2.getLastModifiedAt(), result.getLastModifiedAt())
        );

    }

    @Test
    @DisplayName("팀빌딩 그룹 수정 실패 테스트 - teamBuildingTitle이 1000자 초과")
    public void MODIFY_TEAM_BUILDING_FAIL_CAUSE_TEAM_BUILDING_TITLE_OVERFLOW_TEST(){
        //given
        int teamBuildingId = 1;
        TeamBuildingDto teamBuildingDto = createTeamBuildingDto(teamBuildingId);
        TeamBuildingRequestDto teamBuildingRequestDto = TeamBuildingRequestDto.builder()
                .teamBuildingTitle(createOverflow())
                .build();
        TeamBuildingDto teamBuildingDto2 = teamBuildingDto;

        //when
        Mockito.when(teamBuildingRepository.isExistTeamBuilding(Mockito.anyInt())).thenReturn(true);
        Mockito.when(teamBuildingRepository.readTeamBuildingById(Mockito.anyInt())).thenReturn(teamBuildingDto2);

        //then
        Assertions.assertThrows(ContentOverflowException.class, () ->  teamBuildingManager.updateTeamBuildingTitleById(1, teamBuildingRequestDto.getTeamBuildingTitle()));
    }

    @Test
    @DisplayName("팀빌딩 그룹 수정 실패 테스트 - 없는 teamBuildingId")
    public void MODIFY_TEAM_BUILDING_FAIL_CAUSE_UNKNOWN_TEAM_BUILDING_ID_TEST(){
        //given
        int teamBuildingId = 1;
        TeamBuildingDto teamBuildingDto = createTeamBuildingDto(teamBuildingId);
        TeamBuildingRequestDto teamBuildingRequestDto = TeamBuildingRequestDto.builder()
                .teamBuildingTitle("modified Title")
                .build();
        TeamBuildingDto teamBuildingDto2 = teamBuildingDto;

        //when
        Mockito.when(teamBuildingRepository.isExistTeamBuilding(Mockito.anyInt())).thenReturn(false);
        Mockito.when(teamBuildingRepository.readTeamBuildingById(Mockito.anyInt())).thenReturn(teamBuildingDto2);

        //then
        Assertions.assertThrows(UnknownTeamBuildingIdException.class, () -> teamBuildingManager.updateTeamBuildingTitleById(0,teamBuildingRequestDto.getTeamBuildingTitle()));

    }

    @Test
    @DisplayName("팀빌딩 그룹 삭제 성공 테스트")
    public void DELETE_TEAM_BUILDING_SUCCESS_TEST(){
        //given
        int teamBuildingId = 1;

        //when
        Mockito.when(teamBuildingRepository.isExistTeamBuilding(Mockito.anyInt())).thenReturn(true);

        //then
        Assertions.assertDoesNotThrow(() -> teamBuildingManager.deleteTeamBuildingById(teamBuildingId));

    }

    @Test
    @DisplayName("팀빌딩 그룹 삭제 실패 테스트 - 없는 teamBuildingId")
    public void DELETE_TEAM_BUILDING_FAIL_CAUSE_UNKNOWN_TEAM_BUILDING_ID_TEST(){
        //given
        int teamBuildingId = 0;

        //when
        Mockito.when(teamBuildingRepository.isExistTeamBuilding(Mockito.anyInt())).thenReturn(false);

        //then
        Assertions.assertThrows(UnknownTeamBuildingIdException.class, () -> teamBuildingManager.deleteTeamBuildingById(teamBuildingId));

    }

    private TeamBuildingDto createTeamBuildingDto(int teamBuildingId){
        return TeamBuildingDto.builder()
                .teamBuildingId(teamBuildingId)
                .teamBuildingTitle("hihihi")
                .teamDtoList(createTeamDtoList(teamBuildingId))
                .lastModifiedAt(LocalDateTime.now())
                .build();
    }

    private List<TeamDto> createTeamDtoList(int teamBuildingId){
        List<TeamDto> teamDtoList = new ArrayList<>();
        List<UserDto> userDtoList = createUserDtoList();
        teamDtoList.add(createTeamDto(teamBuildingId, "team 1", List.of(userDtoList.get(0), userDtoList.get(2))));
        teamDtoList.add(createTeamDto(teamBuildingId, "team 2", List.of(userDtoList.get(3), userDtoList.get(4))));
        teamDtoList.add(createTeamDto(teamBuildingId, "team 3", List.of(userDtoList.get(1), userDtoList.get(5))));
        return teamDtoList;
    }

    private List<UserDto> createUserDtoList(){
        List<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(createUserDto("alcuk_id"));
        userDtoList.add(createUserDto("alcuk_id2"));
        userDtoList.add(createUserDto("alcuk_id3"));
        userDtoList.add(createUserDto("alcuk_id4"));
        userDtoList.add(createUserDto("alcuk_id5"));
        userDtoList.add(createUserDto("alcuk_id6"));
        return userDtoList;
    }

    private TeamDto createTeamDto(int teamBuildingId, String teamName, List<UserDto> userDtoList){
        return TeamDto.builder()
                .teamName(teamName)
                .teamBuildingId(teamBuildingId)
                .userDtoList(userDtoList)
                .build();
    }

    private UserDto createUserDto(String userId){
        return UserDto.builder()
                .userId(userId)
                .name("name")
                .build();
    }

    private List<UserRequestDto> createUserRequestDtoList(){
        List<UserRequestDto> userRequestDtoList = new ArrayList<>();
        userRequestDtoList.add(createUserRequestDto("alcuk_id", 2));
        userRequestDtoList.add(createUserRequestDto("alcuk_id2", 1));
        userRequestDtoList.add(createUserRequestDto("alcuk_id3", 3));
        userRequestDtoList.add(createUserRequestDto("alcuk_id4", 4));
        userRequestDtoList.add(createUserRequestDto("alcuk_id5", 2));
        userRequestDtoList.add(createUserRequestDto("alcuk_id6", 10));
        return userRequestDtoList;
    }

    private List<UserRequestDto> createWrongUserRequestDtoList(){
        List<UserRequestDto> userRequestDtoList = new ArrayList<>();
        userRequestDtoList.add(createUserRequestDto("alcuk_id", 2));
        userRequestDtoList.add(createUserRequestDto("alcuk_id2", 1));
        userRequestDtoList.add(createUserRequestDto("alcuk_id3", 3));
        userRequestDtoList.add(createUserRequestDto("alcuk_id4", 4));
        userRequestDtoList.add(createUserRequestDto("alcuk_id5", -1));
        userRequestDtoList.add(createUserRequestDto("alcuk_id6", 10));
        return userRequestDtoList;
    }

    private UserRequestDto createUserRequestDto(String userId, int weight){
        return UserRequestDto.builder()
                .userId(userId)
                .weight(weight)
                .build();
    }

    private String createOverflow(){
        String title = "";
        for (int i = 0; i < 1005; i++){
            title = title + "A";
        }
        return title;
    }

}
